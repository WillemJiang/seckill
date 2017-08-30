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
import io.servicecomb.poc.demo.seckill.event.CouponGrabbedEvent;
import io.servicecomb.poc.demo.seckill.entities.SecKillEventEntity;
import io.servicecomb.poc.demo.seckill.event.SecKillEventFormat;
import io.servicecomb.poc.demo.seckill.event.SecKillEventType;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringSecKillEventRepository;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SecKillRecoveryService<T> {

  private final SpringSecKillEventRepository repository;
  private final SecKillEventFormat secKillEventFormat;

  public SecKillRecoveryService(SpringSecKillEventRepository repository,
      SecKillEventFormat secKillEventFormat) {
    this.repository = repository;
    this.secKillEventFormat = secKillEventFormat;
  }

  public SecKillRecoveryCheckResult<T> check(PromotionEntity promotion) {
    List<SecKillEventEntity> events = this.repository.findByPromotionId(promotion.getPromotionId());
    if (!events.isEmpty()) {
      long count = events.stream()
          .filter(event -> SecKillEventType.CouponGrabbedEvent.equals(event.getType()))
          .count();

      Set<T> claimedCustomers = ConcurrentHashMap.newKeySet();
      claimedCustomers.addAll(events.stream()
          .filter(event -> SecKillEventType.CouponGrabbedEvent.equals(event.getType()))
          .map(event -> ((CouponGrabbedEvent<T>) secKillEventFormat.toSecKillEvent(event)).getCoupon().getCustomerId())
          .collect(Collectors.toSet()));

      boolean isFinished = events.stream()
          .anyMatch(event -> SecKillEventType.PromotionFinishEvent.equals(event.getType()));
      return new SecKillRecoveryCheckResult<>(true, isFinished,
          promotion.getNumberOfCoupons() - (int) count, claimedCustomers);
    }
    return new SecKillRecoveryCheckResult<>(promotion.getNumberOfCoupons());
  }
}
