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

package io.servicecomb.poc.demo.seckill.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.poc.demo.seckill.Coupon;
import io.servicecomb.poc.demo.seckill.SecKillException;
import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import io.servicecomb.poc.demo.seckill.entities.SecKillEventEntity;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JacksonSecKillEventFormat<T> implements SecKillEventFormat {

  protected static ObjectMapper objectMapper = new ObjectMapper();

  private final Map<String, Function<String, SecKillEvent>> eventFactories = new HashMap<String, Function<String, SecKillEvent>>() {{
    put(PromotionStartEvent.class.getSimpleName(),
        (contentJson) -> promotionStartEvent(contentJson));
    put(PromotionFinishEvent.class.getSimpleName(),
        (contentJson) -> promotionFinishEvent(contentJson));
    put(CouponGrabbedEvent.class.getSimpleName(),
        (contentJson) -> couponGrabbedEvent(contentJson));
  }};

  @Override
  public SecKillEvent toSecKillEvent(String eventType, String contentJson) {
    return eventFactories.get(eventType).apply(contentJson);
  }

  @Override
  public SecKillEvent toSecKillEvent(SecKillEventEntity entity) {
    return this.toSecKillEvent(entity.getType(), entity.getContentJson());
  }

  private SecKillEvent promotionStartEvent(String contentJson) {
    try {
      return new PromotionStartEvent(objectMapper.readValue(contentJson, PromotionEntity.class));
    } catch (IOException e) {
      throw new SecKillException("Json Exception", e);
    }
  }

  private SecKillEvent promotionFinishEvent(String contentJson) {
    try {
      return new PromotionFinishEvent(objectMapper.readValue(contentJson, PromotionEntity.class));
    } catch (IOException e) {
      throw new SecKillException("Json Exception", e);
    }
  }

  private SecKillEvent couponGrabbedEvent(String contentJson) {
    try {
      return new CouponGrabbedEvent<>(objectMapper.readValue(contentJson, Coupon.class));
    } catch (IOException e) {
      throw new SecKillException("Json Exception", e);
    }
  }
}
