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

import io.servicecomb.poc.demo.seckill.CouponInfo;
import io.servicecomb.poc.demo.seckill.SecKillManager;
import io.servicecomb.poc.demo.seckill.repositories.CouponInfoRepository;
import io.servicecomb.provider.rest.common.RestSchema;
import java.util.Date;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//use restfull style
@RestSchema(schemaId = "admin")
@RestController
@RequestMapping("/admin")
public class SeckillAdminRESTEndpoint implements SecKillAdminEndpoint {

  private final Logger logger = Logger.getLogger(SeckillAdminRESTEndpoint.class.getName());

  @Autowired
  private SecKillManager secKillManager;

  @Autowired
  private CouponInfoRepository couponInfoRepository;

  @Override
  @RequestMapping(method = RequestMethod.POST, value = "/create")
  public String create(
      @RequestBody CouponCreate create) {
    Date current = new Date();
    if (create.getNumber() > 0 && create.getDiscount() > 0 && create.getDiscount() <= 1 &&
        create.getPublishTime() != null && create.getPublishTime().after(current)
        ) {
      CouponInfo info = new CouponInfo(create.getPublishTime(), create.getNumber(), create.getDiscount());
      couponInfoRepository.save(info);
      logger.info(
          String.format("create a new coupon number = %d discount = %f", create.getNumber(), create.getDiscount()));
      return info.getId();
    } else {
      throw new SecKillException();
    }
  }

  @Override
  @RequestMapping(method = RequestMethod.POST, value = "/start")
  public boolean start(
      @RequestBody CouponStart start) {
    if (start.getCouponId() != null && !start.getCouponId().isEmpty()) {
      //CouponInfo info = new CouponInfo(new Date(),start.getCouponId(),start.getDiscount());
      CouponInfo info = couponInfoRepository.findOne(start.getCouponId());
      if (info != null) {
        secKillManager.start(info);
        logger.info(String.format("star coupon id = %s number = %d discount = %f", info.getId(), info.getCount(), info.getDiscount()));
        return true;
      } else {
        throw new SecKillException();
      }
    } else {
      throw new SecKillException();
    }
  }
}
