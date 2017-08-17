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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.web.PromotionDto;
import java.util.Date;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminServiceApplication.class)
@WebAppConfiguration
public class SecKillAdminApplicationTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private PromotionRepository repository;


  @Before
  public void setup() throws Exception {
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
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
  public void failsWhenNumberOfCouponsIsNotValid() throws Exception {
    mockMvc.perform(post("/admin/promotions/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(0, 0.7f, timeFromNow(2000)))))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid promotion {coupons=")));
  }

  @Test
  public void failsWhenDiscountIsInvalid() throws Exception {
    mockMvc.perform(post("/admin/promotions/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(5, -0.1f, timeFromNow(2000)))))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid promotion {coupons=")));
  }

  @Test
  public void failsWhenPublishTimeIsBeforeNow() throws Exception {
    mockMvc.perform(post("/admin/promotions/").contentType(APPLICATION_JSON)
        .content(toJson(new PromotionDto(5, 0.7f, timeFromNow(-2000)))))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid promotion {coupons=")));
  }

  private String toJson(PromotionDto value) throws JsonProcessingException {
    return objectMapper.writeValueAsString(value);
  }

  private Date timeFromNow(int offset) {
    return new Date(System.currentTimeMillis() + offset);
  }
}
