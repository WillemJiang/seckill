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


import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import io.servicecomb.poc.demo.EventServiceApplication;
import io.servicecomb.poc.demo.seckill.dto.EventMessageDto;
import io.servicecomb.poc.demo.seckill.entities.CouponEntity;
import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import io.servicecomb.poc.demo.seckill.event.CouponGrabbedEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionFinishEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionStartEvent;
import io.servicecomb.poc.demo.seckill.event.SecKillEventFormat;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringCouponRepository;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventServiceApplication.class)
@EnableJms
public class SecKillEventServiceApplicationTest {

  @Autowired
  private SpringPromotionRepository promotionRepository;

  @Autowired
  private SpringCouponRepository<String> couponRepository;

  @Autowired
  private JmsTemplate jmsTemplate;

  @Autowired
  private SecKillEventFormat eventFormat;

  @Before
  public void setUp() throws Exception {
    couponRepository.deleteAll();
    promotionRepository.deleteAll();
  }

  private PromotionEntity promotion1 = generatePromotion();
  private PromotionEntity promotion2 = generatePromotion();

  @Test
  public void receiveCouponEventMessage() throws InterruptedException {
    CouponGrabbedEvent<String> event = new CouponGrabbedEvent<>(promotion1, "zyy");
    sendMessage(eventFormat.toMessage(event));

    event = new CouponGrabbedEvent<>(promotion1, "tester");
    sendMessage(eventFormat.toMessage(event));

    Thread.sleep(300);

    List<CouponEntity<String>> coupons = new ArrayList<>();
    couponRepository.findAll().forEach(coupons::add);

    assertThat(coupons.size(), is(2));
    assertThat(coupons, contains(hasProperty("customerId", is("zyy")), hasProperty("customerId", is("tester"))));
  }

  @Test
  public void receivePromotionEventMessage() throws InterruptedException {
    PromotionStartEvent startEvent = new PromotionStartEvent(promotion1);
    sendMessage(eventFormat.toMessage(startEvent));

    startEvent = new PromotionStartEvent(promotion2);
    sendMessage(eventFormat.toMessage(startEvent));

    Thread.sleep(300);

    List<PromotionEntity> promotions = new ArrayList<>();
    promotionRepository.findAll().forEach(promotions::add);

    assertThat(promotions.size(), is(2));
    assertThat(promotions, contains(hasProperty("promotionId", is(promotion1.getPromotionId())), hasProperty("promotionId", is(promotion2.getPromotionId()))));

    PromotionFinishEvent finishEvent = new PromotionFinishEvent(promotion1);
    sendMessage(eventFormat.toMessage(finishEvent));
    Thread.sleep(300);

    promotions = new ArrayList<>();
    promotionRepository.findAll().forEach(promotions::add);

    assertThat(promotions.size(), is(1));
    assertThat(promotions, contains(hasProperty("promotionId", is(promotion2.getPromotionId()))));
  }


  private void sendMessage(EventMessageDto message) {
    jmsTemplate.convertAndSend("seckill", eventFormat.getFormat().serialize(message));
  }

  private PromotionEntity generatePromotion() {
    return new PromotionEntity(new Date(), new Date(System.currentTimeMillis() + 24 * 3600 * 1000), 10, 0.8f);
  }
}
