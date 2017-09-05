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

package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.dto.EventMessageDto;
import io.servicecomb.poc.demo.seckill.entities.EventEntity;
import io.servicecomb.poc.demo.seckill.event.SecKillEvent;
import io.servicecomb.poc.demo.seckill.event.SecKillEventFormat;
import io.servicecomb.poc.demo.seckill.repositories.SecKillEventRepository;

public class RepositorySecKillEventPersistent implements SecKillEventPersistent {


  private final SecKillEventRepository eventRepository;
  private final SecKillEventFormat eventFormat;
  private final SecKillMessagePublisher messagePublisher;

  public RepositorySecKillEventPersistent(SecKillEventRepository eventRepository,
      SecKillEventFormat eventFormat,
      SecKillMessagePublisher messagePublisher) {

    this.eventRepository = eventRepository;
    this.eventFormat = eventFormat;
    this.messagePublisher = messagePublisher;
  }

  @Override
  public void persistEvent(SecKillEvent event) {
    EventMessageDto message = eventFormat.toMessage(event);
    eventRepository.save(new EventEntity(message.getType(), message.getPromotionId(), message.getContent()));
    messagePublisher.publishMessage(eventFormat.getFormat().serialize(message));
  }
}
