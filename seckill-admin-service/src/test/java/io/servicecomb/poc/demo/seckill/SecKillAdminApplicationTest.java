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
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.poc.demo.seckill.dto.PromotionDto;
import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;
import java.util.Date;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminServiceApplication.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class SecKillAdminApplicationTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SpringPromotionRepository repository;


  @Before
  public void setup() throws Exception {
    repository.deleteAll();
  }

  @Test
  public void createsPromotionSuccessfully() throws Exception {
    MvcResult result = mockMvc.perform(post("/admin/promotions/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(5, 0.7f, timeFromNow(2000)))))
        .andExpect(status().isOk()).andReturn();

    UUID.fromString(result.getResponse().getContentAsString());

    assertThat(repository.count(), is(1L));
  }

  @Test
  public void failsWhenNumberOfCouponsIsInvalid() throws Exception {
    mockMvc.perform(post("/admin/promotions/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(0, 0.7f, timeFromNow(2000)))))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid promotion {numberOfCoupons=")));
  }

  @Test
  public void failsWhenDiscountIsInvalid() throws Exception {
    mockMvc.perform(post("/admin/promotions/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(5, -0.1f, timeFromNow(2000)))))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid promotion {numberOfCoupons=")));
  }

  @Test
  public void updatePromotionSuccessfully() throws Exception {
    MvcResult result = mockMvc.perform(post("/admin/promotions/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(5, 0.7f, timeFromNow(2000)))))
        .andExpect(status().isOk()).andReturn();

    String promotionId = result.getResponse().getContentAsString();
    int numberOfCoupons = 10;
    float discount = 0.8f;
    Date publishTime = truncateToSeconds(new Date());
    Date finishTime = truncateToSeconds(new Date(System.currentTimeMillis() + 300000));

    mockMvc.perform(put("/admin/promotions/" + promotionId + "/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(numberOfCoupons, discount, publishTime, finishTime))))
        .andExpect(status().isOk());

    PromotionEntity promotion = repository.findTopByPromotionId(promotionId);
    assertThat(promotion.getDiscount(), is(discount));
    assertThat(promotion.getNumberOfCoupons(), is(numberOfCoupons));
    assertThat(promotion.getPublishTime().getTime(), is(publishTime.getTime()));
    assertThat(promotion.getFinishTime().getTime(), is(finishTime.getTime()));
  }

  @Test
  public void failsUpdatePromotionWhenPromotionDoesNotExist() throws Exception {
    mockMvc.perform(put("/admin/promotions/" + UUID.randomUUID().toString() + "/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(5, 0.7f, timeFromNow(2000)))))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("PromotionEntity not exists")));
  }

  @Test
  public void failsUpdatePromotionWhenDtoIsInvalid() throws Exception {
    MvcResult result = mockMvc.perform(post("/admin/promotions/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(5, 0.7f, timeFromNow(2000)))))
        .andExpect(status().isOk()).andReturn();

    String promotionId = result.getResponse().getContentAsString();
    int numberOfCoupons = 0;
    float discount = -0.8f;
    Date publishTime = truncateToSeconds(new Date());
    Date finishTime = truncateToSeconds(new Date(System.currentTimeMillis() + 300000));

    mockMvc.perform(put("/admin/promotions/" + promotionId + "/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(numberOfCoupons, discount, publishTime, finishTime))))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid promotion {numberOfCoupons=")));
  }

  private String toJson(PromotionDto value) throws JsonProcessingException {
    return objectMapper.writeValueAsString(value);
  }

  private Date timeFromNow(int offset) {
    return new Date(System.currentTimeMillis() + offset);
  }

  private Date truncateToSeconds(Date date) {
    return new Date((date.getTime() / 1000) * 1000);
  }
}
