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

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.servicecomb.poc.demo.CommandServiceApplication;
import io.servicecomb.poc.demo.seckill.dto.CouponDto;
import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import io.servicecomb.poc.demo.seckill.json.JacksonGeneralFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommandServiceApplication.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class SecKillCommandApplicationTest {

  private final Format format = new JacksonGeneralFormat();
  private final PromotionEntity promotion = new PromotionEntity(new Date(), 10, 0.7f);

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private List<SecKillEventPersistentRunner<String>> persistentRunners;

  @Autowired
  private Map<String, SecKillCommandService<String>> commandServices;

  @Autowired
  private SecKillRecoveryService<String> recoveryService;


  @Autowired
  private SecKillEventPersistent eventPersistent;

  @Before
  public void setUp() throws Exception {
    this.persistentRunners.clear();
    this.commandServices.clear();

    SecKillRecoveryCheckResult<String> recoveryInfo = recoveryService.check(promotion);
    AtomicInteger claimedCoupons = new AtomicInteger();
    BlockingQueue<String> couponQueue = new ArrayBlockingQueue<>(recoveryInfo.remainingCoupons());

    SecKillEventPersistentRunner<String> persistentRunner = new SecKillEventPersistentRunner<>(promotion,
        couponQueue,
        claimedCoupons,
        eventPersistent,
        recoveryInfo);
    persistentRunner.run();
    persistentRunners.add(persistentRunner);

    commandServices
        .put(promotion.getPromotionId(), new SecKillCommandService<>(couponQueue, claimedCoupons, recoveryInfo));
  }

  @Test
  public void grabCouponUseStringCustomerIdSuccessfully() throws Exception {
    mockMvc.perform(post("/command/coupons/").contentType(APPLICATION_JSON)
        .content(format.serialize(new CouponDto<>(promotion.getPromotionId(), "zyy"))))
        .andExpect(status().isOk()).andExpect(content().string("Request accepted"));
  }

  @Test
  public void grabCouponUseIntCustomerIdSuccessfully() throws Exception {
    mockMvc.perform(post("/command/coupons/").contentType(APPLICATION_JSON)
        .content(format.serialize(new CouponDto<>(promotion.getPromotionId(), 10001))))
        .andExpect(status().isOk()).andExpect(content().string("Request accepted"));
  }

  @Test
  public void failsGrabCouponWhenCustomerIdIsInvalid() throws Exception {
    mockMvc.perform(post("/command/coupons/").contentType(APPLICATION_JSON)
        .content(format.serialize(new CouponDto<>(UUID.randomUUID().toString(), "zyy"))))
        .andExpect(status().isBadRequest()).andExpect(content().string(containsString("Invalid promotion")));
  }
}
