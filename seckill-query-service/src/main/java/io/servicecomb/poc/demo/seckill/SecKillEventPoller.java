/*
 *   Copyright 2017 Huawei Technologies Co., Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.servicecomb.poc.demo.seckill;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SecKillEventPoller<T> {

  private final SpringBasedPromotionEventRepository<T> promotionEventRepository;
  private final PromotionRepository promotionRepository;

  private final Map<T, Queue<Coupon<T>>> customerCoupons = new ConcurrentHashMap<>();
  private final Map<String, Promotion> activePromotions = new ConcurrentHashMap<>();
  private final int pollingInterval;
  private int loadedPromotionEventId = 0;

  SecKillEventPoller(
      SpringBasedPromotionEventRepository<T> promotionEventRepository,
      PromotionRepository promotionRepository,
      int pollingInterval) {
    this.promotionEventRepository = promotionEventRepository;
    this.promotionRepository = promotionRepository;
    this.pollingInterval = pollingInterval;
  }

  void reloadEventsScheduler() {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleWithFixedDelay(
        () -> {
          List<PromotionEvent<T>> promotionEvents = promotionEventRepository.findByIdGreaterThan(loadedPromotionEventId);
          populatePromotionEvents(promotionEvents);
        },
        0,
        pollingInterval,
        MILLISECONDS
    );
  }

  public Collection<Coupon<T>> getCustomerCoupons(T customerId) {
    return customerCoupons.get(customerId);
  }

  public Collection<Promotion> getActivePromotions() {
    return activePromotions.values();
  }

  private void populatePromotionEvents(List<PromotionEvent<T>> promotionEvents) {
    Set<String> newActivePromotionIds = ConcurrentHashMap.newKeySet();
    for (PromotionEvent<T> promotionEvent : promotionEvents) {
      if (PromotionEventType.Grab.equals(promotionEvent.getType())) {
        customerCoupons.computeIfAbsent(promotionEvent.getCustomerId(), id -> new ConcurrentLinkedQueue<>())
            .add(new Coupon<>(
                promotionEvent.getId(),
                promotionEvent.getPromotionId(),
                promotionEvent.getTime(),
                promotionEvent.getDiscount(),
                promotionEvent.getCustomerId())
            );
      } else if (PromotionEventType.Start.equals(promotionEvent.getType())) {
        newActivePromotionIds.add(promotionEvent.getPromotionId());
      } else if (PromotionEventType.Finish.equals(promotionEvent.getType())) {
        activePromotions.remove(promotionEvent.getPromotionId());
        newActivePromotionIds.remove(promotionEvent.getPromotionId());
      }
      loadedPromotionEventId = promotionEvent.getId();
    }

    //add new active promotion to cache together
    if (newActivePromotionIds.size() != 0) {
      for (String activePromotionId : newActivePromotionIds) {
        Promotion activePromotion = promotionRepository.findTopByPromotionId(activePromotionId);
        activePromotions.put(activePromotion.getPromotionId(), activePromotion);
      }
    }
  }
}
