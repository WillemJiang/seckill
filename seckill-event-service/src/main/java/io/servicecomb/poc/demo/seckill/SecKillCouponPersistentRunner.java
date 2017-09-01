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

import io.servicecomb.poc.demo.seckill.dto.EventMessageDto;
import io.servicecomb.poc.demo.seckill.event.CouponGrabbedEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionStartEvent;
import io.servicecomb.poc.demo.seckill.event.SecKillEventFormat;
import io.servicecomb.poc.demo.seckill.event.SecKillEventType;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringCouponRepository;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SecKillCouponPersistentRunner<T> {

  private final BlockingQueue<EventMessageDto> messages;
  private final SecKillEventFormat eventFormat;
  private final SpringPromotionRepository promotionRepository;
  private final SpringCouponRepository<T> couponRepository;

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

  public SecKillCouponPersistentRunner(BlockingQueue<EventMessageDto> messages,
      SecKillEventFormat eventFormat,
      SpringPromotionRepository promotionRepository,
      SpringCouponRepository<T> couponRepository) {
    this.messages = messages;
    this.eventFormat = eventFormat;
    this.promotionRepository = promotionRepository;
    this.couponRepository = couponRepository;
  }

  public void run() {
    final Runnable executor = () -> {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          EventMessageDto message = messages.take();
          if (SecKillEventType.CouponGrabbedEvent.equals(message.getType())) {
            couponRepository.save(((CouponGrabbedEvent<T>) eventFormat.fromMessage(message)).getCoupon());
          } else if (SecKillEventType.PromotionStartEvent.equals(message.getType())) {
            promotionRepository.save(((PromotionStartEvent) eventFormat.fromMessage(message)).getPromotion());
          } else if (SecKillEventType.PromotionFinishEvent.equals(message.getType())) {
            promotionRepository.deleteByPromotionId(message.getPromotionId());
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    };

    executorService.execute(executor);
  }
}
