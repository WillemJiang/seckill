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

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class SecKillCommandService<T> {

  private final Queue<T> couponQueue;
  private final AtomicInteger claimedCoupons;
  private final SecKillRecoveryCheckResult<T> recoveryInfo;

  public SecKillCommandService(Queue<T> couponQueue,
      AtomicInteger claimedCoupons,
      SecKillRecoveryCheckResult<T> recoveryInfo) {
    this.couponQueue = couponQueue;
    this.claimedCoupons = claimedCoupons;
    this.recoveryInfo = recoveryInfo;
  }

  public SecKillGrabResult addCouponTo(T customerId) {
    if (recoveryInfo.getClaimedCustomers().add(customerId)) {
      if (claimedCoupons.getAndIncrement() < recoveryInfo.remainingCoupons()) {
        return couponQueue.offer(customerId) ? SecKillGrabResult.Success : SecKillGrabResult.Failed;
      }
      return SecKillGrabResult.Failed;
    }
    return SecKillGrabResult.Duplicate;
  }
}
