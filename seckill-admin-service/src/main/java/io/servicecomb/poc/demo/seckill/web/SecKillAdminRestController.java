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

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.servicecomb.poc.demo.seckill.dto.PromotionDto;
import io.servicecomb.poc.demo.seckill.entities.EventEntity;
import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import io.servicecomb.poc.demo.seckill.event.SecKillEventType;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringSecKillEventRepository;
import io.servicecomb.provider.rest.common.RestSchema;
import io.servicecomb.swagger.invocation.exception.InvocationException;

@RestSchema(schemaId = "seckillAdmin")
@RestController
@RequestMapping("/admin/promotions")
public class SecKillAdminRestController {

  private static final Logger logger = LoggerFactory.getLogger(SecKillAdminRestController.class);

  private final SpringPromotionRepository promotionRepository;

  private final SpringSecKillEventRepository eventRepository;

  @Autowired
  public SecKillAdminRestController(SpringPromotionRepository promotionRepository,
      SpringSecKillEventRepository eventRepository) {
    this.promotionRepository = promotionRepository;
    this.eventRepository = eventRepository;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/")
  public ResponseEntity<String> create(@RequestBody PromotionDto promotionDto) {
    if (isValidPromotion(promotionDto)) {
      PromotionEntity promotion = new PromotionEntity(promotionDto.getPublishTime(), promotionDto.getFinishTime(),
          promotionDto.getNumberOfCoupons(),
          promotionDto.getDiscount());
      promotionRepository.save(promotion);
      logger.info(
          "Created a new promotion id = {}, number = {}, discount = {}, publishTime = {}, finishTime = {}",
          promotion.getPromotionId(),
          promotion.getNumberOfCoupons(),
          promotion.getDiscount(),
          promotion.getPublishTime(),
          promotion.getFinishTime());

      return new ResponseEntity<>(promotion.getPromotionId(), OK);
    }

    throw new InvocationException(BAD_REQUEST, String.format(
        "Invalid promotion {numberOfCoupons=%d, discount=%f, publishTime=%s, finishTime=%s}",
        promotionDto.getNumberOfCoupons(),
        promotionDto.getDiscount(),
        promotionDto.getPublishTime(),
        promotionDto.getFinishTime()));
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/{promotionId}")
  public ResponseEntity<String> modify(@PathVariable String promotionId,
      @RequestBody PromotionDto promotionDto) {
    if (!promotionId.isEmpty() && isValidPromotion(promotionDto)) {
      PromotionEntity promotion = promotionRepository.findTopByPromotionId(promotionId);
      if (promotion != null) {
        List<EventEntity> events = eventRepository.findByPromotionId(promotionId);
        if (events.isEmpty() || events.stream()
            .noneMatch(event -> SecKillEventType.PromotionStartEvent.equals(event.getType()))) {
          promotion.setDiscount(promotionDto.getDiscount());
          promotion.setNumberOfCoupons(promotionDto.getNumberOfCoupons());
          promotion.setPublishTime(promotionDto.getPublishTime());
          promotion.setFinishTime(promotionDto.getFinishTime());
          promotionRepository.save(promotion);
          return new ResponseEntity<>(promotion.getPromotionId(), OK);
        }
        throw new InvocationException(BAD_REQUEST,
            String.format("PromotionEntity had started and changes is rejected {promotionId=%s}", promotionId));
      }
      throw new InvocationException(BAD_REQUEST,
          String.format("PromotionEntity not exists {promotionId=%s}", promotionId));
    }
    throw new InvocationException(BAD_REQUEST, String.format(
        "Invalid promotion {numberOfCoupons=%d, discount=%f, publishTime=%s,, finishTime=%s}",
        promotionDto.getNumberOfCoupons(),
        promotionDto.getDiscount(),
        promotionDto.getPublishTime(),
        promotionDto.getFinishTime()));
  }

  private boolean isValidPromotion(@RequestBody PromotionDto create) {
    return create.getNumberOfCoupons() > 0
        && create.getDiscount() > 0
        && create.getDiscount() <= 1
        && create.getPublishTime() != null
        && create.getFinishTime().getTime() > create.getPublishTime().getTime()
        && create.getFinishTime().getTime() > System.currentTimeMillis();
  }
}
