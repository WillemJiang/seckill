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

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SeckillEventLoader {

  private SpringBasedPromotionEventRepository promotionEventRepository;
  private PromotionRepository promotionRepository;

  private final Map<String, List<Coupon>> customerCoupons = new HashMap<>();
  private final List<Promotion> activePromotions = new ArrayList<>();

  SeckillEventLoader(SpringBasedPromotionEventRepository promotionEventRepository,
      PromotionRepository promotionRepository) {
    this.promotionEventRepository = promotionEventRepository;
    this.promotionRepository = promotionRepository;
  }

  void reloadEventsScheduler() {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleWithFixedDelay(
        new Runnable() {
          int currentPromotionEventIndex = 0;

          @Override
          public void run() {
            List<PromotionEvent> promotionEvents = promotionEventRepository
                .findByIdGreaterThan(currentPromotionEventIndex);

            updateActivePromotions(promotionEvents);

            for (PromotionEvent promotionEvent : promotionEvents) {
              reloadActivePromotions(promotionEvent);
              reloadSuccessCoupons(promotionEvent);

              currentPromotionEventIndex = promotionEvent.getId();
            }
          }
        },
        0,
        1000,
        TimeUnit.MILLISECONDS
    );
  }

  public List<Coupon> getCustomerCoupons(String customerId) {
    return customerCoupons.get(customerId);
  }

  public List<Promotion> getActivePromotions() {
    return activePromotions;
  }

  private void reloadSuccessCoupons(PromotionEvent promotionEvent) {
    customerCoupons.computeIfAbsent((String)promotionEvent.getCustomerId(), customerId -> new ArrayList<>()).add(
        new Coupon<>(promotionEvent.getId(),
            promotionEvent.getPromotionId(),
            promotionEvent.getTime(),
            promotionEvent.getDiscount(),
            promotionEvent.getCustomerId())
    );
  }

  private void updateActivePromotions(List<PromotionEvent> promotionEvents){
    List<PromotionEvent> finishPromotionEvents = promotionEvents.stream().filter(
        promotionEvent -> promotionEvent.getType().equals(PromotionEventType.Finish)).collect(Collectors.toList());

    Iterator<Promotion> promotionIterator = activePromotions.iterator();
    while(promotionIterator.hasNext()){
      String activePromotionId = promotionIterator.next().getPromotionId();

      boolean isMatch = finishPromotionEvents.stream().anyMatch(finishEvent -> finishEvent.getPromotionId().equals(activePromotionId));
      if (isMatch) {
        promotionIterator.remove();
      }
    }
  }

  private void reloadActivePromotions(PromotionEvent promotionEvent) {
    String currentPromotionId = promotionEvent.getPromotionId();

    PromotionEvent startEvent = promotionEventRepository.findTopByPromotionIdAndTypeOrderByIdDesc(
        currentPromotionId, PromotionEventType.Start);
    if (startEvent != null) {
      PromotionEvent finishEvent = promotionEventRepository.findTopByPromotionIdAndTypeOrderByIdDesc(
          currentPromotionId, PromotionEventType.Finish);
      if (finishEvent == null) {
        Promotion activePromotion = promotionRepository.findTopByPromotionId(currentPromotionId);
        activePromotions.add(activePromotion);
      }
    }
  }

}
