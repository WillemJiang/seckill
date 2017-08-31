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

import io.servicecomb.poc.demo.seckill.event.JacksonSecKillEventFormat;
import io.servicecomb.poc.demo.seckill.event.SecKillEventFormat;
import io.servicecomb.poc.demo.seckill.json.JacksonToJsonFormat;
import io.servicecomb.poc.demo.seckill.json.ToJsonFormat;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringSecKillEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SecKillQueryConfig {

  @Bean
  SecKillEventPuller<String> seckillEventLoader(
      SpringSecKillEventRepository secKillEventRepository,
      SpringPromotionRepository promotionRepository,
      SecKillEventFormat secKillEventFormat,
      @Value("${event.polling.interval:500}") int pollingInterval) {

    SecKillEventPuller<String> eventLoader = new SecKillEventPuller<>(
        secKillEventRepository,
        promotionRepository,
        secKillEventFormat,
        pollingInterval);

    eventLoader.reloadEventsScheduler();

    return eventLoader;
  }

  @Bean
  SecKillEventFormat secKillEventFormat() {
    return new JacksonSecKillEventFormat<String>();
  }

  @Bean
  ToJsonFormat toJsonFormat() {
    return new JacksonToJsonFormat();
  }
}
