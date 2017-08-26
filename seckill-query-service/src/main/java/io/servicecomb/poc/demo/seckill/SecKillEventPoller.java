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

import static io.servicecomb.poc.demo.seckill.event.PromotionEventType.Finish;
import static io.servicecomb.poc.demo.seckill.event.PromotionEventType.Grab;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SecKillEventPoller {

  private final SpringBasedPromotionEventRepository promotionEventRepository;
  private final PromotionRepository promotionRepository;

  private final Map<String, Queue<Coupon>> customerCoupons = new ConcurrentHashMap<>();
  private final Map<String, Promotion> activePromotions = new ConcurrentHashMap<>();
  private final int pollingInterval;

  SecKillEventPoller(
      SpringBasedPromotionEventRepository promotionEventRepository,
      PromotionRepository promotionRepository,
      int pollingInterval) {
    this.promotionEventRepository = promotionEventRepository;
    this.promotionRepository = promotionRepository;
    this.pollingInterval = pollingInterval;
  }

  void reloadEventsScheduler() {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleWithFixedDelay(
        new Runnable() {
          private int lastPromotionEventId = 0;

          @Override
          public void run() {
            List<PromotionEvent> promotionEvents = promotionEventRepository.findByIdGreaterThan(lastPromotionEventId);

            removeFinishedPromotions(promotionEvents);

            for (PromotionEvent promotionEvent : promotionEvents) {
              populateNewPromotions(promotionEvent);
              populateGrabbedCoupons(promotionEvent);

              lastPromotionEventId = promotionEvent.getId();
            }
          }
        },
        0,
        pollingInterval,
        MILLISECONDS
    );
  }

  public Collection<Coupon> getCustomerCoupons(String customerId) {
    return customerCoupons.get(customerId);
  }

  public Collection<Promotion> getActivePromotions() {
    return activePromotions.values();
  }

  private void populateGrabbedCoupons(PromotionEvent promotionEvent) {
    if (Grab.equals(promotionEvent.getType())) {
      customerCoupons.computeIfAbsent(promotionEvent.getCustomerId().toString(), id -> new ConcurrentLinkedQueue<>())
          .add(new Coupon<>(
              promotionEvent.getId(),
              promotionEvent.getPromotionId(),
              promotionEvent.getTime(),
              promotionEvent.getDiscount(),
              promotionEvent.getCustomerId())
          );
    }
  }

  private void removeFinishedPromotions(List<PromotionEvent> promotionEvents) {
    promotionEvents.stream()
        .filter(promotionEvent -> promotionEvent.getType().equals(PromotionEventType.Finish))
        .forEach(promotionEvent -> activePromotions.remove(promotionEvent.getPromotionId()));
  }

  private void populateNewPromotions(PromotionEvent promotionEvent) {
    String currentPromotionId = promotionEvent.getPromotionId();

    // TODO: 8/26/2017 can we do with a single query?
    PromotionEvent startEvent = promotionEventRepository.findTopByPromotionIdAndTypeOrderByIdDesc(
        currentPromotionId, PromotionEventType.Start);
    if (startEvent != null) {
      PromotionEvent finishEvent = promotionEventRepository.findTopByPromotionIdAndTypeOrderByIdDesc(
          currentPromotionId, Finish);
      if (finishEvent == null) {
        Promotion activePromotion = promotionRepository.findTopByPromotionId(currentPromotionId);
        activePromotions.put(activePromotion.getPromotionId(), activePromotion);
      }
    }
  }
}
