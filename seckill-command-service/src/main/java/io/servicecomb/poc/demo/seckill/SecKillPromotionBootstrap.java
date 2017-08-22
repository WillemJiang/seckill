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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecKillPromotionBootstrap {

  private static final Logger logger = LoggerFactory.getLogger(SecKillPromotionBootstrap.class);

  private final PromotionRepository promotionRepository;
  private final PromotionEventRepository eventRepository;
  private final Map<String, SecKillCommandService<String>> commandServices;
  private final List<SecKillPersistentRunner<String>> persistentRunners;
  private final SecKillRecoveryService recoveryService;
  private final AtomicInteger claimedCoupons = new AtomicInteger();

  public SecKillPromotionBootstrap(
      PromotionRepository promotionRepository,
      PromotionEventRepository eventRepository,
      Map<String, SecKillCommandService<String>> commandServices,
      List<SecKillPersistentRunner<String>> persistentRunners,
      SecKillRecoveryService recoveryService) {
    this.promotionRepository = promotionRepository;
    this.eventRepository = eventRepository;
    this.commandServices = commandServices;
    this.persistentRunners = persistentRunners;
    this.recoveryService = recoveryService;
  }

  public Promotion startUpPromotion() {
    Iterable<Promotion> promotions = promotionRepository.findAll();
    for (Promotion promotion : promotions) {
      if (promotion.getPublishTime().before(new Date())) {
        return promotion;
      }
    }
    return null;
  }

  public void run() {
    CompletableFuture.runAsync(() -> {
      boolean promotionLoaded = false;
      while (!Thread.currentThread().isInterrupted() && !promotionLoaded) {
        try {
          Promotion promotion = startUpPromotion();
          if (promotion != null) {
            BlockingQueue<String> couponQueue = new ArrayBlockingQueue<>(promotion.getNumberOfCoupons());

            SecKillRecoveryCheckResult recoveryInfo = recoveryService.check(promotion);
            SecKillPersistentRunner<String> persistentRunner = new SecKillPersistentRunner<>(promotion,
                couponQueue,
                claimedCoupons,
                eventRepository,
                recoveryInfo);
            persistentRunners.add(persistentRunner);
            persistentRunner.run();

            commandServices.put(promotion.getPromotionId(), new SecKillCommandService<>(couponQueue,
                claimedCoupons,
                recoveryInfo));
            promotionLoaded = true;

            logger.info("Promotion started = {}", promotion);
          }
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
  }
}
