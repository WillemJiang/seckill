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
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedCouponEventRepository;
import java.util.List;
import java.util.stream.Collectors;

public class SecKillRecoveryService {

  private final SpringBasedCouponEventRepository repository;

  public SecKillRecoveryService(SpringBasedCouponEventRepository repository) {
    this.repository = repository;
  }

  public SeckillRecoveryCheckResult check(Promotion promotion) {
    SeckillRecoveryCheckResult result = new SeckillRecoveryCheckResult(promotion.getNumberOfCoupons());
    PromotionEvent<String> lastStart = this.repository.findTopByCouponIdAndTypeOrderByIdDesc(promotion.getId(),
        PromotionEventType.Start);
    if (lastStart != null) {
      result.setStartEventAvailable(true);
      List<PromotionEvent<String>> allEvents = this.repository.findByCouponIdAndIdGreaterThan(promotion.getId(), lastStart.getId());
      if (allEvents.size() != 0) {
        result.setFinishEventAvailable(allEvents.stream().anyMatch(event -> PromotionEventType.Finish.equals(event.getType())));
        long count = allEvents.stream().filter(event -> PromotionEventType.Grab.equals(event.getType())).count();
        result.setRemainingCoupons(promotion.getNumberOfCoupons() - (int) count);
        result.getClaimedCustomers().addAll(allEvents.stream().filter(event -> !PromotionEventType.Finish.equals(event.getType()))
            .map(PromotionEvent::getCustomerId).collect(Collectors.toSet()));
      }
    }
    return result;
  }
}
