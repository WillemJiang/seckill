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
import io.servicecomb.poc.demo.seckill.entities.CouponEntity;
import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import io.servicecomb.poc.demo.seckill.event.CouponGrabbedEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionFinishEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionStartEvent;
import io.servicecomb.poc.demo.seckill.json.JacksonGeneralFormat;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringCouponRepository;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;
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

  private final PromotionEntity promotion1 = generatePromotion();
  private final PromotionEntity promotion2 = generatePromotion();
  private final PromotionEntity promotion3 = generatePromotion();

  private final PromotionEntity[] promotions = {promotion1, promotion2, promotion3};

  @Autowired
  private SpringPromotionRepository promotionRepository;

  @Autowired
  private SpringCouponRepository couponRepository;

  @Autowired
  private MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    couponRepository.deleteAll();
    promotionRepository.deleteAll();
  }

  @Test
  public void activePromotionCanBeQueried() throws Exception {
    addActivePromotion(promotion1);
    addActivePromotion(promotion3);

    Thread.sleep(300);

    mockMvc.perform(get("/query/promotions/").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(
            allOf(
                containsString(promotion1.getPromotionId()),
                containsString(promotion3.getPromotionId()),
                not(containsString(promotion2.getPromotionId())))));
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

  private void addCouponToCustomer(String customerId, PromotionEntity promotion) {
    couponRepository.save(new CouponEntity<>(promotion.getPromotionId(),System.currentTimeMillis(),promotion.getDiscount(),customerId));
  }

  private void addActivePromotion(PromotionEntity promotion) {
    promotionRepository.save(promotion);
  }

  private PromotionEntity generatePromotion() {
    return new PromotionEntity(new Date(), new Date(System.currentTimeMillis() + 24 * 3600 * 1000), 1, 0.8f);
  }
}