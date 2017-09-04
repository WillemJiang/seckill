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

import io.servicecomb.poc.demo.seckill.Format;
import io.servicecomb.poc.demo.seckill.dto.EventMessageDto;
import io.servicecomb.poc.demo.seckill.entities.EventEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SecKillEventFormat {

  private final Format format;


  private final Map<String, Function<String, SecKillEvent>> eventFactories = new HashMap<String, Function<String, SecKillEvent>>() {{
    put(CouponGrabbedEvent.class.getSimpleName(), (content) -> couponGrabbedEvent(content));
    put(PromotionStartEvent.class.getSimpleName(), (content) -> promotionStartEvent(content));
    put(PromotionFinishEvent.class.getSimpleName(), (content) -> promotionFinishEvent(content));
  }};

  public Format getFormat() {
    return format;
  }

  public SecKillEventFormat(Format format) {
    this.format = format;
  }

  public SecKillEvent fromMessage(EventMessageDto message) {
    return generateEvent(message.getType(), message.getContent());
  }

  public SecKillEvent fromEntity(EventEntity entity) {
    return generateEvent(entity.getType(), entity.getContent());
  }

  public EventMessageDto toMessage(SecKillEvent event) {
    return new EventMessageDto(event.getType(), event.getPromotionId(), event.getContent(format));
  }

  public EventEntity toEntity(SecKillEvent event) {
    return new EventEntity(event.getType(), event.getPromotionId(), event.getContent(format));
  }

  private SecKillEvent generateEvent(String type, String content) {
    return eventFactories.get(type).apply(content);
  }

  private SecKillEvent couponGrabbedEvent(String content) {
    return new CouponGrabbedEvent(format, content);
  }

  private SecKillEvent promotionStartEvent(String content) {
    return new PromotionStartEvent(format, content);
  }

  private SecKillEvent promotionFinishEvent(String content) {
    return new PromotionFinishEvent(format, content);
  }
}
