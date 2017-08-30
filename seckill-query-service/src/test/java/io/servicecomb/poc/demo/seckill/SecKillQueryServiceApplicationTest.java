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

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.servicecomb.poc.demo.QueryServiceApplication;
import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import io.servicecomb.poc.demo.seckill.event.CouponGrabbedEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionFinishEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionStartEvent;
import io.servicecomb.poc.demo.seckill.json.ToJsonFormat;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringSecKillEventRepository;
import java.time.ZonedDateTime;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = QueryServiceApplication.class, properties = "event.polling.interval=100")
@AutoConfigureMockMvc
public class SecKillQueryServiceApplicationTest {

  private static final String customerId = "tester";

  private final PromotionEntity promotion1 = promotion();
  private final PromotionEntity promotion2 = promotion();
  private final PromotionEntity promotion3 = promotion();

  private final PromotionEntity[] promotions = {promotion1, promotion2, promotion3};

  @Autowired
  private SpringPromotionRepository promotionRepository;

  @Autowired
  private SpringSecKillEventRepository eventRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ToJsonFormat toJsonFormat;

  @Before
  public void setUp() throws Exception {
    eventRepository.deleteAll();
    promotionRepository.deleteAll();
  }

  @Test
  public void grabbedCouponsCanBeQueried() throws Exception {
    addCouponToCustomer(customerId, promotion1);
    addCouponToCustomer("unknown", promotion2);

    Thread.sleep(300);

    mockMvc.perform(get("/query/coupons/{customerId}", customerId).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(
            allOf(
                containsString(promotion1.getPromotionId()),
                containsString(customerId),
                not(containsString(promotion2.getPromotionId())),
                not(containsString("unknown")))));

    addCouponToCustomer(customerId, promotion3);

    Thread.sleep(300);

    mockMvc.perform(get("/query/coupons/{customerId}", customerId).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(
            allOf(
                containsString(promotion1.getPromotionId()),
                containsString(promotion3.getPromotionId()),
                containsString(customerId),
                not(containsString(promotion2.getPromotionId())),
                not(containsString("unknown")))));
  }

  @Test
  public void retrievesActivePromotionsOnly() throws Exception {
    for (PromotionEntity promotion : promotions) {
      promotionRepository.save(promotion);
    }

    eventRepository.save(new PromotionStartEvent(promotion1).toEntity(toJsonFormat));
    eventRepository.save(new PromotionStartEvent<>(promotion2).toEntity(toJsonFormat));

    Thread.sleep(300);

    mockMvc.perform(get("/query/promotions").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(
            allOf(
                containsString(promotion1.getPromotionId()),
                containsString(promotion2.getPromotionId()))
        ));

    eventRepository.save(new PromotionFinishEvent(promotion2).toEntity(toJsonFormat));
    eventRepository.save(new PromotionStartEvent(promotion3).toEntity(toJsonFormat));

    Thread.sleep(300);

    mockMvc.perform(get("/query/promotions").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(
            allOf(
                not(containsString(promotion2.getPromotionId())),
                containsString(promotion1.getPromotionId()),
                containsString(promotion3.getPromotionId()))
        ));
  }

  private void addCouponToCustomer(String customerId, PromotionEntity promotion) {
    eventRepository.save(new PromotionStartEvent(promotion).toEntity(toJsonFormat));

    CouponGrabbedEvent<String> grabbedEvent = new CouponGrabbedEvent<>(promotion, customerId);
    eventRepository.save(grabbedEvent.toEntity(toJsonFormat));

    eventRepository.save(new PromotionFinishEvent(promotion).toEntity(toJsonFormat));
  }

  private PromotionEntity promotion() {
    ZonedDateTime startTime = ZonedDateTime.now();
    ZonedDateTime finishTime = startTime.plusDays(1);

    return new PromotionEntity(toDate(startTime), toDate(finishTime), 1, 0.8f);
  }

  private Date toDate(ZonedDateTime startTime) {
    return Date.from(startTime.toInstant());
  }
}