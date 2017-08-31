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

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecKillEventConfig {

  @Bean
  SecKillMessageSubscriber secKillMessageSubscriber() {
    return new RabbitSecKillMessageSubscriber();
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(MessageBrokerName.Rabbit_ExchangeName, true, false);
  }

  @Bean
  public Queue queue() {
    return new Queue(MessageBrokerName.Rabbit_QueueName, true);
  }

  @Bean
  public Binding binding(TopicExchange exchange, Queue queue) {
    return BindingBuilder.bind(queue).to(exchange).with(MessageBrokerName.Rabbit_TopicName);
  }

}
