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
import io.servicecomb.poc.demo.seckill.event.PromotionFinishEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionStartEvent;
import io.servicecomb.poc.demo.seckill.event.SecKillEvent;
import io.servicecomb.poc.demo.seckill.event.SecKillEventFormat;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringCouponRepository;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

public class ActiveMQSecKillMessageSubscriber<T> implements SecKillMessageSubscriber {

  private static final Logger logger = LoggerFactory.getLogger(ActiveMQSecKillMessageSubscriber.class);
  private final SecKillEventFormat eventFormat;

  private final SpringPromotionRepository promotionRepository;
  private final SpringCouponRepository<T> couponRepository;

  private final Map<String, Consumer<SecKillEvent>> eventFactories = new HashMap<String, Consumer<SecKillEvent>>() {{
    put(CouponGrabbedEvent.class.getSimpleName(), (event) -> processCouponGrabbedEvent(event));
    put(PromotionStartEvent.class.getSimpleName(), (event) -> processPromotionStartEvent(event));
    put(PromotionFinishEvent.class.getSimpleName(), (event) -> processPromotionFinishEvent(event));
  }};

  public ActiveMQSecKillMessageSubscriber(SpringPromotionRepository promotionRepository,
      SpringCouponRepository<T> couponRepository, SecKillEventFormat eventFormat) {
    this.promotionRepository = promotionRepository;
    this.couponRepository = couponRepository;
    this.eventFormat = eventFormat;
  }

  @Override
  @JmsListener(destination = "seckill", containerFactory = "containerFactory")
  public void subscribeMessage(String messageContent) {
    logger.info("receive message : {}", messageContent);
    EventMessageDto message = eventFormat.getFormat().deserialize(messageContent, EventMessageDto.class);
    SecKillEvent event = eventFormat.fromMessage(message);
    eventFactories.get(event.getType()).accept(event);
  }

  private void processCouponGrabbedEvent(SecKillEvent event) {
    couponRepository.save(((CouponGrabbedEvent<T>) event).getCoupon());
  }

  private void processPromotionStartEvent(SecKillEvent event) {
    promotionRepository.save(((PromotionStartEvent) event).getPromotion());
  }

  private void processPromotionFinishEvent(SecKillEvent event) {
    promotionRepository.deleteByPromotionId(event.getPromotionId());
  }

}
