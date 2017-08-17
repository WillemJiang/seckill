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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import io.servicecomb.poc.demo.seckill.Promotion;
import io.servicecomb.poc.demo.seckill.dto.PromotionDto;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/promotions")
public class SecKillAdminRestController {

  private static final Logger logger = LoggerFactory.getLogger(SecKillAdminRestController.class);

  private final PromotionRepository promotionRepository;

  @Autowired
  public SecKillAdminRestController(PromotionRepository promotionRepository) {
    this.promotionRepository = promotionRepository;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/")
  public ResponseEntity<String> create(@RequestBody PromotionDto promotionDto) {
    if (isValidPromotion(promotionDto)) {
      Promotion promotion = new Promotion(promotionDto.getPublishTime(), promotionDto.getNumberOfCoupons(),
          promotionDto.getDiscount());
      promotionRepository.save(promotion);
      logger.info(
          "Created a new promotion id = {}, number = {}, discount = {}, publishTime = {}",
          promotion.getId(),
          promotion.getNumberOfCoupons(),
          promotion.getDiscount(),
          promotion.getPublishTime());

      return new ResponseEntity<>(promotion.getId(), OK);
    }

    return new ResponseEntity<>(String.format(
        "Invalid promotion {coupons=%d, discount=%f, publishTime=%s}",
        promotionDto.getNumberOfCoupons(),
        promotionDto.getDiscount(),
        promotionDto.getPublishTime()),
        BAD_REQUEST);
  }

  private boolean isValidPromotion(@RequestBody PromotionDto create) {
    return create.getNumberOfCoupons() > 0
        && create.getDiscount() > 0
        && create.getDiscount() <= 1
        && create.getPublishTime() != null
        && create.getPublishTime().after(new Date());
  }
}
