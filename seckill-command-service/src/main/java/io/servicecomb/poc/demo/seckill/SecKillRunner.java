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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecKillRunner {

  private static final Logger logger = LoggerFactory.getLogger(SecKillRunner.class);

  private final PromotionRepository promotionRepository;
  private final CouponEventRepository eventRepository;
  private final AtomicInteger claimedCoupons = new AtomicInteger();
  private final ObjectMapper jsonMapper;

  private SecKillPersistentRunner persistentRunner;
  private SecKillCommandService commandService;
  private BlockingQueue<String> couponQueue;

  public SecKillCommandService getCommandService() {
    return commandService;
  }

  protected void setCommandService(SecKillCommandService commandService) {
    this.commandService = commandService;
  }

  public SecKillRunner(PromotionRepository promotionRepository, CouponEventRepository eventRepository) {
    this.promotionRepository = promotionRepository;
    this.eventRepository = eventRepository;
    this.jsonMapper = new ObjectMapper();
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

  @Deprecated
  public Promotion startUpPromotion(Promotion defaultIfMissing) {
    Promotion promotion = startUpPromotion();
    return promotion != null ? promotion : defaultIfMissing;
  }

  public void run() {
    CompletableFuture.runAsync(() -> {
      boolean promotionLoaded = false;
      while (!Thread.currentThread().isInterrupted() && !promotionLoaded) {
        try {
          Promotion promotion = startUpPromotion();
          if (promotion != null) {
            this.couponQueue = new ArrayBlockingQueue<>(promotion.getNumberOfCoupons());

            SeckillRecoveryCheckResult recoveryInfo = new SeckillRecoveryCheckResult(promotion.getNumberOfCoupons());
            this.persistentRunner = new SecKillPersistentRunner(promotion,
                couponQueue,
                claimedCoupons,
                eventRepository,
                recoveryInfo);
            this.persistentRunner.run();

            this.commandService = new SecKillCommandService(promotion,
                couponQueue,
                claimedCoupons,
                recoveryInfo.getClaimedCustomers());
            promotionLoaded = true;

            logger.info("Promotion started = {}", this.jsonMapper.writeValueAsString(promotion));
          }
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
