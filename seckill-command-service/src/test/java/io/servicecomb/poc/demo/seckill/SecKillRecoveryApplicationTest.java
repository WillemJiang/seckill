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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.poc.demo.CommandServiceApplication;
import io.servicecomb.poc.demo.seckill.dto.CouponDto;
import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionGrabbedEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionStartEvent;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
public class SecKillRecoveryApplicationTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final Promotion promotion = new Promotion(new Date(), 10, 0.7f);

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SpringBasedPromotionEventRepository<String> eventRepository;

  @Autowired
  private PromotionRepository promotionRepository;


  @Test
  public void recoveryServiceSuccessfully() throws Exception {
    //init failed promotion status
    List<PromotionEvent> events = new ArrayList<>();
    events.add(new PromotionStartEvent(promotion));
    events.add(new PromotionGrabbedEvent<>(promotion, "0"));
    events.add(new PromotionGrabbedEvent<>(promotion, "2"));
    events.add(new PromotionGrabbedEvent<>(promotion, "4"));
    events.add(new PromotionGrabbedEvent<>(promotion, "6"));
    events.add(new PromotionGrabbedEvent<>(promotion, "8"));
    eventRepository.save(events);
    promotionRepository.save(promotion);

    Thread.sleep(2000);

    for (int i = 0; i < 11; i++) {
      if (i == 10) {
        mockMvc.perform(post("/command/coupons/").contentType(APPLICATION_JSON)
            .content(toJson(new CouponDto<>(promotion.getPromotionId(), i))))
            .andExpect(status().is(429))
            .andExpect(content().string(containsString("out of stock")));
      } else if (i % 2 == 0) {
        mockMvc.perform(post("/command/coupons/").contentType(APPLICATION_JSON)
            .content(toJson(new CouponDto<>(promotion.getPromotionId(), i))))
            .andExpect(status().is(429))
            .andExpect(content().string(containsString("duplicate order")));
      } else if (i % 2 == 1) {
        mockMvc.perform(post("/command/coupons/").contentType(APPLICATION_JSON)
            .content(toJson(new CouponDto<>(promotion.getPromotionId(), i))))
            .andExpect(status().isOk());
      }
    }

    mockMvc.perform(post("/command/coupons/").contentType(APPLICATION_JSON)
        .content(toJson(new CouponDto<>(UUID.randomUUID().toString(), "zyy"))))
        .andExpect(status().isBadRequest()).andExpect(content().string(containsString("Invalid promotion")));
  }

  private String toJson(CouponDto value) throws JsonProcessingException {
    return objectMapper.writeValueAsString(value);
  }
}
