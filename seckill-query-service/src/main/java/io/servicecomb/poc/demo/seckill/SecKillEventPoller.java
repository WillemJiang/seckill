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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

import io.servicecomb.poc.demo.seckill.entities.CouponEntity;
import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringCouponRepository;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringPromotionRepository;

public class SecKillEventPoller<T> {

  private final SpringCouponRepository<T> couponRepository;

  private final SpringPromotionRepository promotionRepository;

  private final Map<T, Queue<CouponEntity<T>>> customerCoupons = new ConcurrentHashMap<>();

  private volatile List<PromotionEntity> activePromotions = new LinkedList<>();

  private final int pollingInterval;

  private int loadedCouponEntityId = 0;

  SecKillEventPoller(
      SpringCouponRepository<T> couponRepository,
      SpringPromotionRepository promotionRepository,
      int pollingInterval) {
    this.couponRepository = couponRepository;
    this.promotionRepository = promotionRepository;
    this.pollingInterval = pollingInterval;
  }

  void reloadScheduler() {
    final Runnable executor = () -> {
      populateCoupons();
      reloadActivePromotions();
    };
    Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(executor, 0, pollingInterval, MILLISECONDS);
  }

  public Collection<CouponEntity<T>> getCustomerCoupons(T customerId) {
    return customerCoupons.getOrDefault(customerId, null);
  }

  public Collection<PromotionEntity> getActivePromotions() {
    return activePromotions;
  }

  private void populateCoupons() {
    List<CouponEntity<T>> couponEntities = couponRepository.findByIdGreaterThan(loadedCouponEntityId);
    for (CouponEntity<T> couponEntity : couponEntities) {
      customerCoupons.computeIfAbsent(couponEntity.getCustomerId(), id -> new ConcurrentLinkedQueue<>())
          .add(couponEntity);
      loadedCouponEntityId = couponEntity.getId();
    }
  }

  private void reloadActivePromotions() {
    List<PromotionEntity> promotions = new LinkedList<>();
    promotionRepository.findAll().forEach(promotions::add);
    activePromotions = promotions;
  }
}
