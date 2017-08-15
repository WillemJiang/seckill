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

import io.servicecomb.poc.demo.seckill.SecKillManager;
import io.servicecomb.provider.rest.common.RestSchema;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//use restfull style
@RestSchema(schemaId = "command")
@RestController
@RequestMapping("/command")
public class SeckillCommandRESTEndpoint implements SeckillCommandEndpoint {

  private final Logger logger = Logger.getLogger(SeckillCommandRESTEndpoint.class.getName());

  @Autowired
  private SecKillManager secKillManager;

  @Override
  @RequestMapping(method = RequestMethod.POST,value = "/seckill")
  public boolean seckill(
      @RequestBody CouponSecKill secKill) {
    if(secKill.getCustomerId() != null) {
      boolean result = secKillManager.seckill(secKill.getCustomerId().toString());
      logger.info(String.format("seckill from = %s result = %s", secKill.getCustomerId(), result));
      return result;
    } else {
      return false;
    }
  }
}
