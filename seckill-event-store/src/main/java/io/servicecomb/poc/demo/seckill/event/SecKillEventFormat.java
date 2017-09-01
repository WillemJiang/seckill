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

public class SecKillEventFormat {

  private Format format = null;

  public Format getFormat() {
    return format;
  }

  public SecKillEventFormat(Format format) {
    this.format = format;
  }

  public SecKillEvent fromMessage(EventMessageDto message) {
    return generateEvent(message.getType(), message.getContentJson());
  }

  public SecKillEvent fromEntity(EventEntity entity) {
    return generateEvent(entity.getType(), entity.getContentJson());
  }

  private SecKillEvent generateEvent(String type, String contentJson) {
    if (SecKillEventType.CouponGrabbedEvent.equals(type)) {
      return new CouponGrabbedEvent(format, contentJson);
    } else if (SecKillEventType.PromotionStartEvent.equals(type)) {
      return new PromotionStartEvent(format, contentJson);
    } else if (SecKillEventType.PromotionFinishEvent.equals(type)) {
      return new PromotionFinishEvent(format, contentJson);
    } else {
      return null;
    }
  }


  public EventMessageDto toMessage(SecKillEvent event) {
    return new EventMessageDto(event.getType(), event.getPromotionId(), event.getContent(format));
  }

  public EventEntity toEntity(SecKillEvent event) {
    return new EventEntity(event.getType(), event.getPromotionId(), event.getContent(format));
  }
}
