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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.servicecomb.poc.demo.seckill.SecKillCommandService;
import io.servicecomb.poc.demo.seckill.SecKillGrabResult;
import io.servicecomb.poc.demo.seckill.dto.CouponDto;
import io.servicecomb.provider.rest.common.RestSchema;
import io.servicecomb.swagger.invocation.exception.CommonExceptionData;
import io.servicecomb.swagger.invocation.exception.InvocationException;

@RestSchema(schemaId = "seckillCommand")
@RestController
@RequestMapping("/command/coupons")
public class SecKillCommandRestController {

  private static final Logger logger = LoggerFactory.getLogger(SecKillCommandRestController.class);

  private final Map<String, SecKillCommandService<String>> commandServices;

  @Autowired
  public SecKillCommandRestController(Map<String, SecKillCommandService<String>> commandServices) {
    this.commandServices = commandServices;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/")
  public ResponseEntity<String> seckill(
      @RequestBody CouponDto couponDto) {
    if (isValidCoupon(couponDto)) {
      if (commandServices.containsKey(couponDto.getPromotionId())) {
        SecKillGrabResult result = commandServices.get(couponDto.getPromotionId())
            .addCouponTo(couponDto.getCustomerId());
        logger.info("SecKill from = {}, result = {}", couponDto.getCustomerId(), result);
        if (result == SecKillGrabResult.Success) {
          return new ResponseEntity<>("Request accepted", HttpStatus.OK);
        } else if (result == SecKillGrabResult.Failed) {
          throw new InvocationException(429, "Too Many Requests",
              new CommonExceptionData("Request rejected due to coupon out of stock"));
        } else {
          throw new InvocationException(429, "Too Many Requests",
              new CommonExceptionData("Request rejected duplicate order"));
        }
      } else {
        throw new InvocationException(BAD_REQUEST,
            String.format("Invalid promotion {promotion=%s}", couponDto.getPromotionId()));
      }
    } else {
      throw new InvocationException(BAD_REQUEST, "Invalid coupon {promotionId is null or customerId is null}");
    }
  }

  private boolean isValidCoupon(@RequestBody CouponDto couponDto) {
    return couponDto.getCustomerId() != null && couponDto.getPromotionId() != null;
  }
}
