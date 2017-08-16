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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.poc.demo.CommandServiceApplication;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionRepository;
import io.servicecomb.poc.demo.seckill.web.PromotionCreate;
import java.nio.charset.Charset;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommandServiceApplication.class)
@WebAppConfiguration
public class SecKillCommandServiceApplicationTest {

  private MockMvc mockMvc;

  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private SpringBasedPromotionRepository repository;


  @Before
  public void setup() throws Exception {
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
    repository.deleteAll();
  }

  @Test
  public void testCreatePromotion() throws Exception {
    MvcResult result = this.mockMvc.perform(post("/admin/create").contentType(contentType)
        .content(new ObjectMapper()
            .writeValueAsString(new PromotionCreate(5, 0.7f, new Date(System.currentTimeMillis() + 2000)))))
        .andExpect(status().isOk()).andReturn();
    assertThat(result.getResponse().getContentLength(), is(36));

    assertThat(repository.count(), is(1L));

    this.mockMvc.perform(post("/admin/create").contentType(contentType)
        .content(new ObjectMapper()
            .writeValueAsString(new PromotionCreate(0, 0.7f, new Date(System.currentTimeMillis() + 2000)))))
        .andExpect(status().is4xxClientError());

    this.mockMvc.perform(post("/admin/create").contentType(contentType)
        .content(new ObjectMapper()
            .writeValueAsString(new PromotionCreate(5, -0.1f, new Date(System.currentTimeMillis() + 2000)))))
        .andExpect(status().is4xxClientError());

    this.mockMvc.perform(post("/admin/create").contentType(contentType)
        .content(new ObjectMapper()
            .writeValueAsString(new PromotionCreate(5, 0.7f, new Date(System.currentTimeMillis() - 2000)))))
        .andExpect(status().is4xxClientError());
  }
}
