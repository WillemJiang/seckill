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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.poc.demo.seckill.dto.CouponDto;
import io.servicecomb.poc.demo.seckill.dto.PromotionDto;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntegrationTestApplication.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class SecKillIntegrationTestApplicationTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PromotionRepository promotionRepository;

  @Autowired
  private SecKillRunner secKillRunner;


  @Before
  public void setup() throws Exception {
    this.promotionRepository.deleteAll();
    this.secKillRunner.run();
  }

  @Test
  public void createPromotionAndGrabSuccessfully() throws Exception {
    mockMvc.perform(post("/admin/promotions/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(5, 0.7f, new Date()))))
        .andExpect(status().isOk());

    Thread.sleep(2000);

    mockMvc.perform(post("/command/coupons/").contentType(APPLICATION_JSON)
        .content(toJson(new CouponDto("zyy"))))
        .andExpect(status().isOk()).andExpect(content().string("Request accepted"));

    Thread.sleep(2000);
  }

  private String toJson(Object value) throws JsonProcessingException {
    return objectMapper.writeValueAsString(value);
  }
}
