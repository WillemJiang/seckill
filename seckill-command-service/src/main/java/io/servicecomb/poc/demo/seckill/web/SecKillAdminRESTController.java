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

import io.servicecomb.poc.demo.seckill.Promotion;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionRepository;
import java.util.Date;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class SecKillAdminRESTController implements SecKillAdminController {

  private final Logger logger = Logger.getLogger(SecKillAdminRESTController.class.getName());

  private SpringBasedPromotionRepository promotionRepository;
  private SecKillException exception;

  @Autowired
  public SecKillAdminRESTController(SpringBasedPromotionRepository promotionRepository,SecKillException exception) {
    this.promotionRepository = promotionRepository;
    this.exception = exception;
  }

  @Override
  @RequestMapping(method = RequestMethod.POST, value = "/create")
  public String create(
      @RequestBody PromotionCreate create) {
    if (create.getNumberOfCoupons() > 0 && create.getDiscount() > 0 && create.getDiscount() <= 1 &&
        create.getPublishTime() != null && create.getPublishTime().after(new Date())
        ) {
      Promotion promotion = new Promotion(create.getPublishTime(), create.getNumberOfCoupons(), create.getDiscount());
      promotionRepository.save(promotion);
      logger.info(
          String.format("create a new promotion number = %d discount = %f", create.getNumberOfCoupons(),
              create.getDiscount()));
      return promotion.getId();
    } else {
      throw exception;
    }
  }
}
