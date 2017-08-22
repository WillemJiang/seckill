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

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SecKillRecoveryService {

  private final SpringBasedPromotionEventRepository repository;

  public SecKillRecoveryService(SpringBasedPromotionEventRepository repository) {
    this.repository = repository;
  }

  public SecKillRecoveryCheckResult check(Promotion promotion) {
    List<PromotionEvent<String>> events = this.repository.findByPromotionId(promotion.getPromotionId());
    if (!events.isEmpty()) {
      long count = events.stream()
          .filter(event -> PromotionEventType.Grab.equals(event.getType()))
          .count();

      Set<String> claimedCustomers = events.stream()
          .filter(event -> PromotionEventType.Grab.equals(event.getType()))
          .map(PromotionEvent::getCustomerId)
          .collect(Collectors.toSet());

      boolean isFinished = events.stream().anyMatch(event -> PromotionEventType.Finish.equals(event.getType()));
      return new SecKillRecoveryCheckResult(true, isFinished,
          promotion.getNumberOfCoupons() - (int) count, claimedCustomers);
    }
    return new SecKillRecoveryCheckResult(promotion.getNumberOfCoupons());
  }
}
