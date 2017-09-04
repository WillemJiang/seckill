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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

public class ActiveMQSecKillMessagePublisher implements SecKillMessagePublisher {

  private static final Logger logger = LoggerFactory.getLogger(ActiveMQSecKillMessagePublisher.class);
  private final JmsTemplate jmsTemplate;

  public ActiveMQSecKillMessagePublisher(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  @Override
  public void publishMessage(String messageContent) {
    logger.info("publish message : {}", messageContent);
    jmsTemplate.convertAndSend("seckill", messageContent);
  }
}
