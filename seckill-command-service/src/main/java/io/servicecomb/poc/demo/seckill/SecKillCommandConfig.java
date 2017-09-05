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

import io.servicecomb.poc.demo.seckill.entities.EventEntity;
import io.servicecomb.poc.demo.seckill.event.SecKillEventFormat;
import io.servicecomb.poc.demo.seckill.json.JacksonGeneralFormat;
import io.servicecomb.poc.demo.seckill.repositories.SecKillEventRepository;
import io.servicecomb.poc.demo.seckill.repositories.SecKillEventRepositoryImpl;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringSecKillEventRepository;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.jms.core.JmsTemplate;

@Configuration
class SecKillCommandConfig {

  @Bean
  Map<String, SecKillCommandService<String>> commandServices() {
    return new HashMap<>();
  }

  @Bean
  List<SecKillEventPersistentRunner<String>> persistentRunners() {
    return new LinkedList<>();
  }

  @Bean
  SecKillEventRepository secKillEventRepository(
      PagingAndSortingRepository<EventEntity, Integer> repository) {
    return new SecKillEventRepositoryImpl(repository);
  }

  @Bean
  SecKillEventPersistent eventPersistent(SecKillEventRepository eventRepository,
      SecKillEventFormat eventFormat,
      SecKillMessagePublisher messagePublisher) {
    return new RepositorySecKillEventPersistent(eventRepository, eventFormat, messagePublisher);
  }

  @Bean
  SecKillPromotionBootstrap<String> secKillPromotionBootstrap(SpringPromotionRepository promotionRepository,
      Map<String, SecKillCommandService<String>> commandServices,
      List<SecKillEventPersistentRunner<String>> persistentRunners,
      SecKillEventPersistent eventPersistent,
      SecKillRecoveryService<String> recoveryService) {
    SecKillPromotionBootstrap<String> promotionBootstrap = new SecKillPromotionBootstrap<>(promotionRepository,
        commandServices,
        persistentRunners,
        eventPersistent,
        recoveryService);
    promotionBootstrap.run();
    return promotionBootstrap;
  }

  @Bean
  SecKillRecoveryService<String> secKillRecoveryService(SpringSecKillEventRepository eventRepository,
      SecKillEventFormat eventFormat) {
    return new SecKillRecoveryService<>(eventRepository, eventFormat);
  }

  @Bean
  SecKillEventFormat secKillEventFormat(Format format) {
    return new SecKillEventFormat(format);
  }

  @Bean
  Format format() {
    return new JacksonGeneralFormat();
  }

  @Bean
  SecKillMessagePublisher secKillMessagePublisher(JmsTemplate jmsTemplate) {
    return new ActiveMQSecKillMessagePublisher(jmsTemplate);
  }

}
