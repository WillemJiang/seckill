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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class SeckillEventLoader<T> {
  private SpringBasedPromotionEventRepository promotionEventRepository;
  private PromotionRepository promotionRepository;

  SeckillEventLoader(SpringBasedPromotionEventRepository promotionEventRepository,
      PromotionRepository promotionRepository){
    this.promotionEventRepository = promotionEventRepository;
    this.promotionRepository = promotionRepository;
  }

  void reloadEventsScheduler() {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    executor.scheduleWithFixedDelay(new Runnable() {
                                      int currentPromotionEventIndex = 0;
                                      @Override
                                      public void run() {
                                        currentPromotionEventIndex = reloadActivePromotions(currentPromotionEventIndex);
                                      }
                                    },
        0,
        1000,
        TimeUnit.MILLISECONDS);

    executor.scheduleWithFixedDelay(new Runnable() {
                                      int currentPromotionEventIndex = 0;
                                      @Override
                                      public void run() {
                                        currentPromotionEventIndex = reloadSuccessCoupons(currentPromotionEventIndex);
                                      }
                                    },
        0,
        1000,
        TimeUnit.MILLISECONDS);
  }

  private final Map<T,List<Coupon>> customerCoupons = new HashMap<>();
  private final List<Promotion> activePromotions = new ArrayList<>();

  List<Coupon> getCustomerCoupons(T customerId){
    return customerCoupons.get(customerId);
  }
  List<Promotion> getActivePromotions(){
    return activePromotions;
  }

  private int reloadSuccessCoupons(int promotionEventIndex){
    List<PromotionEvent> promotionEvents = promotionEventRepository.findByIdGreaterThan(promotionEventIndex);

    for (PromotionEvent promotionEvent : promotionEvents) {
      T customerId = (T) promotionEvent.getCustomerId();

      if(!customerCoupons.containsKey(customerId)) {
        customerCoupons.put(customerId, new ArrayList<>());
      }

      customerCoupons.get(customerId).add(
          new Coupon(promotionEvent.getId(),
              promotionEvent.getPromotionId(),
              promotionEvent.getTime(),
              promotionEvent.getDiscount(),
              promotionEvent.getCustomerId())
      );

      promotionEventIndex = promotionEvent.getId();
    }

    return promotionEventIndex;
  }

  private int reloadActivePromotions (int promotionEventIndex) {
    List<PromotionEvent> promotionEvents = promotionEventRepository.findByIdGreaterThan(promotionEventIndex);

    for (Promotion activePromotion : activePromotions) {
      for (PromotionEvent promotionEvent : promotionEvents) {
        if(activePromotion.getPromotionId().equals(promotionEvent.getPromotionId())
            && promotionEvent.getType().equals(PromotionEventType.Finish)) {
          activePromotions.remove(activePromotion);
        }
      }
    }

    for (PromotionEvent promotionEvent : promotionEvents) {
      String currentPromotionId = promotionEvent.getPromotionId();

      PromotionEvent startEvent = promotionEventRepository.findTopByPromotionIdAndTypeOrderByIdDesc(
          currentPromotionId, PromotionEventType.Start);
      if (startEvent != null) {
        PromotionEvent finishEvent = promotionEventRepository.findTopByPromotionIdAndTypeOrderByIdDesc(
            currentPromotionId, PromotionEventType.Finish);
        if(finishEvent == null){
          Promotion activePromotion = promotionRepository.findTopByPromotionId(currentPromotionId);
          activePromotions.add(activePromotion);
        }
      }

      promotionEventIndex = promotionEvent.getId();
    }

    return promotionEventIndex;
  }
}
