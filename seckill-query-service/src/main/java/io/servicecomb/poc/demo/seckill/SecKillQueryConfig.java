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

import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecKillQueryConfig {

  @Bean
  SecKillEventSubscriber secKillEventSubscriber(SeckillEventLoader seckillEventLoader) {
    return new SecKillEventSubscriber(seckillEventLoader);
  }

  @Bean
  SeckillEventLoader seckillEventLoader(SpringBasedPromotionEventRepository promotionEventRepository,
      PromotionRepository promotionRepository) {

    SeckillEventLoader eventLoader = new SeckillEventLoader(promotionEventRepository,promotionRepository);

    eventLoader.reloadEventsScheduler();

    return eventLoader;
  }
}
