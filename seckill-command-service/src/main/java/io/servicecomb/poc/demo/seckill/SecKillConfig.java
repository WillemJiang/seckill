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

import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedCouponEventRepository;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
class SecKillConfig {

  private final AtomicInteger claimedCoupons = new AtomicInteger();
  private final int remainingCoupons = 10;
  private SecKillRunner secKillRunner = null;

  @Bean()
  SecKillRunner secKillRunner(PromotionRepository promotionRepository, CouponEventRepository eventRepository) {
    secKillRunner = new SecKillRunner(promotionRepository, eventRepository);
    return secKillRunner;
  }

  @Bean
  @DependsOn("secKillRunner")
  Promotion promotion() {
    return secKillRunner.startUpPromotion(new Promotion(new Date(),remainingCoupons,0.7f));
  }

  @Bean
  SeckillRecoveryCheckResult recoveryCheckResult() {
    return new SeckillRecoveryCheckResult(remainingCoupons);
  }

  @Bean
  SecKillPersistentRunner<String> secKillPersistentRunner(Promotion promotion,
      SpringBasedCouponEventRepository repository,
      SeckillRecoveryCheckResult recoveryInfo,
      BlockingQueue<String> couponQueue) {

    SecKillPersistentRunner<String> persistentRunner = new SecKillPersistentRunner<>(promotion,
        couponQueue,
        claimedCoupons,
        repository,
        recoveryInfo);
    persistentRunner.run();
    return persistentRunner;
  }

  @Bean
  SecKillCommandService<String> secKillCommandService(Promotion promotion,
      SeckillRecoveryCheckResult recoveryInfo,
      BlockingQueue<String> couponQueue) {

    SecKillCommandService commandService = new SecKillCommandService<>(promotion,
        couponQueue,
        claimedCoupons,
        recoveryInfo.getClaimedCustomers());
    secKillRunner.run(commandService);
    return commandService;
  }

  @Bean
  BlockingQueue<String> couponQueue(Promotion promotion) {
    return new ArrayBlockingQueue<>(promotion.getNumberOfCoupons());
  }
}
