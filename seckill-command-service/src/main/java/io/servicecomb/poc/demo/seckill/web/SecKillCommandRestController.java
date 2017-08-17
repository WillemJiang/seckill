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

import io.servicecomb.poc.demo.seckill.SecKillCommandService;
import io.servicecomb.poc.demo.seckill.SecKillPersistentRunner;
import io.servicecomb.poc.demo.seckill.dto.CouponDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/command/coupons")
public class SecKillCommandRestController {

  private static final Logger logger = LoggerFactory.getLogger(SecKillCommandRestController.class);

  private final SecKillCommandService commandService;
  private final SecKillPersistentRunner persistentRunner;

  @Autowired
  public SecKillCommandRestController(SecKillCommandService secKillCommandService,
      SecKillPersistentRunner secKillPersistentRunner) {
    this.commandService = secKillCommandService;
    this.persistentRunner = secKillPersistentRunner;
    this.persistentRunner.run();
  }


  @RequestMapping(method = RequestMethod.POST, value = "/")
  public boolean seckill(
      @RequestBody CouponDto couponDto) {
    if (isValidCoupon(couponDto)) {
      boolean result = this.commandService.addCouponTo(couponDto.getCustomerId());
      logger.info("SecKill from = {}, result = {}", couponDto.getCustomerId(), result);
      return result;
    } else {
      return false;
    }
  }

  private boolean isValidCoupon(@RequestBody CouponDto couponDto) {
    return couponDto.getCustomerId() != null;
  }
}
