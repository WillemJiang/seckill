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
import io.servicecomb.poc.demo.seckill.event.PromotionFinishEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionGrabbedEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionStartEvent;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
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

  private final Promotion promotion1 = promotion();
  private final Promotion promotion2 = promotion();
  private final Promotion promotion3 = promotion();

  private final Promotion[] promotions = {promotion1, promotion2, promotion3};

  @Autowired
  private PromotionRepository promotionRepository;

  @Autowired
  private SpringBasedPromotionEventRepository<String> promotionEventRepository;

  @Autowired
  private MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    promotionEventRepository.deleteAll();
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
    for (Promotion promotion : promotions) {
      promotionRepository.save(promotion);
    }

    promotionEventRepository.save(new PromotionStartEvent<>(promotion1));
    promotionEventRepository.save(new PromotionStartEvent<>(promotion2));

    Thread.sleep(300);

    mockMvc.perform(get("/query/promotions").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(
            allOf(
                containsString(promotion1.getPromotionId()),
                containsString(promotion2.getPromotionId()))
        ));

    promotionEventRepository.save(new PromotionFinishEvent<>(promotion2));
    promotionEventRepository.save(new PromotionStartEvent<>(promotion3));

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

  private String addCouponToCustomer(String customerId, Promotion promotion) {
    promotionEventRepository.save(new PromotionStartEvent<>(promotion));

    PromotionGrabbedEvent<String> grabbedEvent = new PromotionGrabbedEvent<>(promotion, customerId);
    promotionEventRepository.save(grabbedEvent);

    promotionEventRepository.save(new PromotionFinishEvent<>(promotion));
    return grabbedEvent.getPromotionId();
  }

  private Promotion promotion() {
    ZonedDateTime startTime = ZonedDateTime.now();
    ZonedDateTime finishTime = startTime.plusDays(1);

    return new Promotion(toDate(startTime), toDate(finishTime), 1, 0.8f);
  }

  private Date toDate(ZonedDateTime startTime) {
    return Date.from(startTime.toInstant());
  }
}