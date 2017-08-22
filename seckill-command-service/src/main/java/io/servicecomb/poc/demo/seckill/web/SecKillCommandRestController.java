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

import io.servicecomb.poc.demo.seckill.SecKillCommandService;
import io.servicecomb.poc.demo.seckill.dto.CouponDto;
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
      @RequestBody CouponDto<String> couponDto) {
    if (isValidCoupon(couponDto)) {
      if (commandServices.containsKey(couponDto.getPromotionId())) {
        int result = commandServices.get(couponDto.getPromotionId()).addCouponTo(couponDto.getCustomerId());
        logger.info("SecKill from = {}, result = {}", couponDto.getCustomerId(), result);
        if (result == 0) {
          return new ResponseEntity<>("Request accepted", HttpStatus.OK);
        } else if (result == 1) {
          return new ResponseEntity<>("Request rejected due to coupon out of stock", HttpStatus.OK);
        } else {
          return new ResponseEntity<>("Request rejected duplicate order", HttpStatus.OK);
        }
      } else {
        return new ResponseEntity<>(String.format("Invalid promotion {promotion=%s}", couponDto.getPromotionId()), BAD_REQUEST);
      }
    } else {
      return new ResponseEntity<>("Invalid coupon {customerId is null}", BAD_REQUEST);
    }
  }

  private boolean isValidCoupon(@RequestBody CouponDto couponDto) {
    return couponDto.getCustomerId() != null;
  }
}
