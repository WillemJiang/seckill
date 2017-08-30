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

import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import io.servicecomb.poc.demo.seckill.entities.SecKillEventEntity;
import io.servicecomb.poc.demo.seckill.event.CouponGrabbedEvent;
import io.servicecomb.poc.demo.seckill.event.SecKillEventFormat;
import io.servicecomb.poc.demo.seckill.event.SecKillEventType;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringSecKillEventRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SecKillEventPuller<T> {

  private final SpringSecKillEventRepository eventRepository;
  private final SpringPromotionRepository promotionRepository;

  private final Map<T, Queue<Coupon<T>>> customerCoupons = new ConcurrentHashMap<>();
  private final Map<String, PromotionEntity> activePromotions = new ConcurrentHashMap<>();
  private final SecKillEventFormat secKillEventFormat;
  private final int pollingInterval;
  private int loadedPromotionEventId = 0;

  SecKillEventPuller(
      SpringSecKillEventRepository secKillEventRepository,
      SpringPromotionRepository promotionRepository,
      SecKillEventFormat eventFormat,
      int pollingInterval) {
    this.eventRepository = secKillEventRepository;
    this.promotionRepository = promotionRepository;
    this.secKillEventFormat = eventFormat;
    this.pollingInterval = pollingInterval;
  }

  void reloadEventsScheduler() {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleWithFixedDelay(
        () -> {
          List<SecKillEventEntity> eventEntities = eventRepository.findByIdGreaterThan(loadedPromotionEventId);
          populateEventEntities(eventEntities);
        },
        0,
        pollingInterval,
        MILLISECONDS
    );
  }

  public Collection<Coupon<T>> getCustomerCoupons(T customerId) {
    return customerCoupons.get(customerId);
  }

  public Collection<PromotionEntity> getActivePromotions() {
    return activePromotions.values();
  }

  private void populateEventEntities(List<SecKillEventEntity> eventEntities) {
    Set<String> newActivePromotionIds = new HashSet<>();
    for (SecKillEventEntity eventEntity : eventEntities) {
      if (SecKillEventType.CouponGrabbedEvent.equals(eventEntity.getType())) {
        CouponGrabbedEvent<T> event = (CouponGrabbedEvent<T>) secKillEventFormat.toSecKillEvent(eventEntity);
        customerCoupons.computeIfAbsent(event.getCoupon().getCustomerId(), id -> new ConcurrentLinkedQueue<>())
            .add(event.getCoupon());
      } else if (SecKillEventType.PromotionStartEvent.equals(eventEntity.getType())) {
        newActivePromotionIds.add(eventEntity.getPromotionId());
      } else if (SecKillEventType.PromotionFinishEvent.equals(eventEntity.getType())) {
        activePromotions.remove(eventEntity.getPromotionId());
        newActivePromotionIds.remove(eventEntity.getPromotionId());
      }
      loadedPromotionEventId = eventEntity.getId();
    }

    //add new active promotion to cache together
    if (!newActivePromotionIds.isEmpty()) {
      promotionRepository.findByPromotionIdIn(newActivePromotionIds)
          .forEach(promotion -> activePromotions.put(promotion.getPromotionId(), promotion));
    }
  }
}
