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

package io.servicecomb.poc.demo.seckill.web;

import io.servicecomb.poc.demo.seckill.Coupon;
import io.servicecomb.poc.demo.seckill.SecKillEventPuller;
import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/query")
public class SeckillQueryRestController {

  private static final Logger logger = LoggerFactory.getLogger(SeckillQueryRestController.class);

  @Autowired
  private SecKillEventPuller<String> secKillEventPoller;

  @RequestMapping(method = RequestMethod.GET, value = "/coupons/{customerId}")
  public Collection<Coupon<String>> querySuccess(@PathVariable("customerId") String customerId) {
    logger.info("Query customer id = {} coupons", customerId);
    return secKillEventPoller.getCustomerCoupons(customerId);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/promotions")
  public Collection<PromotionEntity> queryCurrent() {
    logger.info("Query current active promotions");
    return secKillEventPoller.getActivePromotions();
  }
}
