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

import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecKillPromotionBootstrap<T> {

  private static final Logger logger = LoggerFactory.getLogger(SecKillPromotionBootstrap.class);

  private final SpringPromotionRepository promotionRepository;
  private final Map<String, SecKillCommandService<T>> commandServices;
  private final List<SecKillEventPersistentRunner<T>> persistentRunners;
  private final SecKillEventPersistent eventPersistent;
  private final SecKillRecoveryService<T> recoveryService;

  private final Map<String, PromotionEntity> waitingPromotions = new HashMap<>();
  private int loadedPromotionId = 0;

  public SecKillPromotionBootstrap(
      SpringPromotionRepository promotionRepository,
      Map<String, SecKillCommandService<T>> commandServices,
      List<SecKillEventPersistentRunner<T>> persistentRunners,
      SecKillEventPersistent eventPersistent,
      SecKillRecoveryService<T> recoveryService) {
    this.promotionRepository = promotionRepository;
    this.commandServices = commandServices;
    this.persistentRunners = persistentRunners;
    this.eventPersistent = eventPersistent;
    this.recoveryService = recoveryService;
  }

  public void run() {
    final Runnable executor = () -> {
      Iterable<PromotionEntity> promotions = promotionRepository.findByIdGreaterThan(loadedPromotionId);
      for (PromotionEntity promotion : promotions) {
        if (promotion.getPublishTime().getTime() <= System.currentTimeMillis()) {
          startUpPromotion(promotion);
          logger.info("PromotionEntity started = {}", promotion);
        } else {
          waitingPromotions.put(promotion.getPromotionId(), promotion);
        }
        loadedPromotionId = promotion.getId();
      }

      for (String promotionId : waitingPromotions.keySet()) {
        PromotionEntity promotion = waitingPromotions.get(promotionId);
        if (promotion.getPublishTime().getTime() <= System.currentTimeMillis()) {
          startUpPromotion(promotion);
          logger.info("PromotionEntity started = {}", promotion);
          waitingPromotions.remove(promotionId);
        }
      }
    };

    Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(executor, 0, 500, TimeUnit.MILLISECONDS);
  }

  private void startUpPromotion(PromotionEntity promotion) {
    SecKillRecoveryCheckResult<T> recoveryInfo = recoveryService.check(promotion);
    if(!recoveryInfo.isFinished()) {
      AtomicInteger claimedCoupons = new AtomicInteger();
      BlockingQueue<T> couponQueue = new ArrayBlockingQueue<>(promotion.getNumberOfCoupons());
      SecKillEventPersistentRunner<T> persistentRunner = new SecKillEventPersistentRunner<>(promotion,
          couponQueue,
          claimedCoupons,
          eventPersistent,
          recoveryInfo);
      persistentRunners.add(persistentRunner);
      persistentRunner.run();

      commandServices.put(promotion.getPromotionId(), new SecKillCommandService<>(couponQueue,
          claimedCoupons,
          recoveryInfo));
    }
  }
}
