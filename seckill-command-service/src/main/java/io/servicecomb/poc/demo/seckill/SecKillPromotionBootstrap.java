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

import io.servicecomb.poc.demo.seckill.repositories.PromotionEventRepository;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecKillPromotionBootstrap<T> {

  private static final Logger logger = LoggerFactory.getLogger(SecKillPromotionBootstrap.class);

  private final PromotionRepository promotionRepository;
  private final PromotionEventRepository<T> eventRepository;
  private final Map<String, SecKillCommandService<T>> commandServices;
  private final List<SecKillPersistentRunner<T>> persistentRunners;
  private final SecKillRecoveryService<T> recoveryService;

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
  private int loadedPromotionId = 0;
  private final Map<String, Promotion> waitingPromotions = new HashMap<>();

  public SecKillPromotionBootstrap(
      PromotionRepository promotionRepository,
      PromotionEventRepository<T> eventRepository,
      Map<String, SecKillCommandService<T>> commandServices,
      List<SecKillPersistentRunner<T>> persistentRunners,
      SecKillRecoveryService<T> recoveryService) {
    this.promotionRepository = promotionRepository;
    this.eventRepository = eventRepository;
    this.commandServices = commandServices;
    this.persistentRunners = persistentRunners;
    this.recoveryService = recoveryService;
  }

  public void run() {
    final Runnable executor = () -> {
      Iterable<Promotion> promotions = promotionRepository.findByIdGreaterThan(loadedPromotionId);
      for (Promotion promotion : promotions) {
        if (promotion.getPublishTime().getTime() <= System.currentTimeMillis()) {
          startUpPromotion(promotion);
          logger.info("Promotion started = {}", promotion);
        } else {
          waitingPromotions.put(promotion.getPromotionId(), promotion);
        }
        loadedPromotionId = promotion.getId();
      }

      for (String promotionId : waitingPromotions.keySet()) {
        Promotion promotion = waitingPromotions.get(promotionId);
        if (promotion.getPublishTime().getTime() <= System.currentTimeMillis()) {
          startUpPromotion(promotion);
          logger.info("Promotion started = {}", promotion);
          waitingPromotions.remove(promotionId);
        }
      }
    };

    executorService.scheduleWithFixedDelay(executor, 0, 500, TimeUnit.MILLISECONDS);
  }

  private void startUpPromotion(Promotion promotion) {
    AtomicInteger claimedCoupons = new AtomicInteger();
    BlockingQueue<T> couponQueue = new ArrayBlockingQueue<>(promotion.getNumberOfCoupons());
    SecKillRecoveryCheckResult<T> recoveryInfo = recoveryService.check(promotion);
    SecKillPersistentRunner<T> persistentRunner = new SecKillPersistentRunner<>(promotion,
        couponQueue,
        claimedCoupons,
        eventRepository,
        recoveryInfo);
    persistentRunners.add(persistentRunner);
    persistentRunner.run();

    commandServices.put(promotion.getPromotionId(), new SecKillCommandService<>(couponQueue,
        claimedCoupons,
        recoveryInfo));
  }
}
