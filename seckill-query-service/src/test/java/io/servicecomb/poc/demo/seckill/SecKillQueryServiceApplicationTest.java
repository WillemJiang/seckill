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

import io.servicecomb.poc.demo.CommandQueryApplication;
import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionFinishEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionGrabbedEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionStartEvent;
import io.servicecomb.poc.demo.seckill.repositories.PromotionEventRepository;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommandQueryApplication.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class SecKillQueryServiceApplicationTest {
  @Autowired
  private PromotionRepository promotionRepository;

  @Autowired
  private PromotionEventRepository promotionEventRepository;

  @Autowired
  private MockMvc mockMvc;

  private MediaType contentType = new MediaType(
      MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8")
  );

  @Test
  public void testQuerySuccess() throws Exception {
    List<String> expectCouponList = new ArrayList<>();

    for (int i = 1; i <= 10; i++) {
      Date startTime = new Date();
      Date finishTime = new Date(startTime.getTime() + 60*1000);

      Promotion promotionTest = new Promotion(startTime,finishTime,i,0.8f);

      PromotionStartEvent<String> startEvent = new PromotionStartEvent<>(promotionTest);
      promotionEventRepository.save(startEvent);

      if((i > 2) && (i < 6)) {
        PromotionGrabbedEvent<String> grabbedEvent = new PromotionGrabbedEvent<>(
            promotionTest,
            "tester");
        promotionEventRepository.save(grabbedEvent);
        expectCouponList.add(promotionTest.getPromotionId());
      }

      if(i < 5) {
        PromotionFinishEvent<String> finishEvent = new PromotionFinishEvent<>(promotionTest);
        promotionEventRepository.save(finishEvent);
      }
    }

    Thread.sleep(2000);
    this.mockMvc.perform(get("/query/coupons/nonCustomerId").contentType(contentType))
        .andExpect(status().isOk()).andExpect(content().string(containsString("")));

    ResultActions resultFill = this.mockMvc.perform(get("/query/coupons/tester").contentType(contentType));
    for (String s : expectCouponList) {
      resultFill.andExpect(content().string(containsString(s)));
    }

//    numberOfCoupons in each promotion
    for (int i = 3; i < 6; i++) {
      resultFill.andExpect(content().string(containsString(Integer.toString(i))));
    }
  }

  @Test
  public void testQueryCurrent() throws Exception {
//    test null promotions
    this.mockMvc.perform(get("/query/promotions").contentType(contentType))
        .andExpect(content().string(containsString("")));

//    inject test promotion
    List<Promotion> expectPromotionList = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      Date startTime = new Date();
      Date finishTime = new Date(startTime.getTime() + 60*1000);

      Promotion promotionTest = new Promotion(startTime, finishTime, i,0.7f);
      promotionRepository.save(promotionTest);
      expectPromotionList.add(promotionTest);

      PromotionStartEvent<String> startEvent = new PromotionStartEvent<>(promotionTest);
      promotionEventRepository.save(startEvent);
    }

    Thread.sleep(2000);
//    check the query result wether is matching
    ResultActions resultFill = this.mockMvc.perform(get("/query/promotions").contentType(contentType));
    for (Promotion promotion : expectPromotionList) {
      resultFill.andExpect(content().string(containsString(Integer.toString(promotion.getId()))));
    }
/*

//    test expire promotion
    for (Promotion promotion : expectPromotionList) {
      PromotionFinishEvent<String> finishEvent = new PromotionFinishEvent<>(promotion);
      promotionEventRepository.save(finishEvent);
    }

    Thread.sleep(2000);
    ResultActions result = this.mockMvc.perform(get("/query/promotions").contentType(contentType));
*/
  }

}