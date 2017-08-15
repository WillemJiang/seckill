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

import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import io.servicecomb.poc.demo.seckill.event.CouponEventType;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedCouponEventRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecKillManager<T> {
  private SecKillController secKillController = null;

  @Autowired
  private SpringBasedCouponEventRepository repository;

  public synchronized SeckillRecoveryCheckResult recoveryCheck(CouponInfo info) {
    SeckillRecoveryCheckResult result = new SeckillRecoveryCheckResult(info.getCount());
    CouponEvent lastStart = this.repository.findTopByCouponIdAndTypeOrderByIdDesc(info.getId(),
        CouponEventType.Start);
    if (lastStart != null) {
      result.setStartEventAvailable(true);
      List<CouponEvent> allEvents = this.repository.findByCouponIdAndIdGreaterThan(info.getId(), lastStart.getId());
      if (allEvents.size() != 0) {
        result.setFinishEventAvailable(allEvents.stream().anyMatch(event -> CouponEventType.Finish.equals(event.getType())));
        long count = allEvents.stream().map(event -> CouponEventType.SecKill.equals(event.getType())).count();

        result.setLeftCount(lastStart.getCount() - (int) count);
        result.getClaimedCustomers().addAll(allEvents.stream().filter(event -> !CouponEventType.Finish.equals(event.getType()))
            .map(event -> event.getCustomerId()).distinct().collect(Collectors.toSet()));
      }
    }
    return result;
  }

  public synchronized boolean start(CouponInfo info) {
    SeckillRecoveryCheckResult result = recoveryCheck(info);
    if(!result.isFinishEventAvailable()) {
      if (secKillController != null)
        secKillController.finish();
      secKillController = new SecKillController(info, repository,result);
      return true;
    }
    return false;
  }

  public boolean seckill(T customerId) {
    return secKillController != null && secKillController.seckill(customerId);
  }
}
