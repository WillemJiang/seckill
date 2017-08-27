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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SecKillRecoveryCheckResult<T> {
  private final boolean startEventAvailable;
  private final boolean finishEventAvailable;
  private final int remainingCoupons;
  private final Set<T> claimedCustomers;

  public boolean isStarted() {
    return startEventAvailable;
  }

  public int remainingCoupons() {
    return remainingCoupons;
  }

  public boolean isFinished() {
    return finishEventAvailable;
  }

  public Set<T> getClaimedCustomers() {
    return claimedCustomers;
  }

  public SecKillRecoveryCheckResult(int remainingCoupons) {
    startEventAvailable = false;
    finishEventAvailable = false;
    this.remainingCoupons = remainingCoupons;
    this.claimedCustomers = ConcurrentHashMap.newKeySet();
  }

  public SecKillRecoveryCheckResult(boolean startEventAvailable, boolean finishEventAvailable, int remainingCoupons,
      Set<T> claimedCustomers) {
    this.startEventAvailable = startEventAvailable;
    this.finishEventAvailable = finishEventAvailable;
    this.remainingCoupons = remainingCoupons;
    this.claimedCustomers = claimedCustomers;
  }
}
