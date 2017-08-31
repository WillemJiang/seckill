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

package io.servicecomb.poc.demo.seckill.json;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.poc.demo.seckill.SecKillException;
import io.servicecomb.poc.demo.seckill.entities.CouponEntity;
import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;

public class JacksonToJsonFormat implements ToJsonFormat {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public JacksonToJsonFormat() {
    objectMapper.setVisibility(
        objectMapper.getSerializationConfig()
            .getDefaultVisibilityChecker()
            .withFieldVisibility(ANY)
            .withGetterVisibility(NONE)
            .withSetterVisibility(NONE)
            .withCreatorVisibility(NONE));
  }

  @Override
  public String toJson(PromotionEntity promotion) {
    try {
      return objectMapper.writeValueAsString(promotion);
    } catch (JsonProcessingException e) {
      throw new SecKillException("Json Exception", e);
    }
  }

  @Override
  public String toJson(CouponEntity coupon) {
    try {
      return objectMapper.writeValueAsString(coupon);
    } catch (JsonProcessingException e) {
      throw new SecKillException("Json Exception", e);
    }
  }
}