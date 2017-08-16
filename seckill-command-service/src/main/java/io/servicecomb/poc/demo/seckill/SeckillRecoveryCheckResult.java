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

import java.util.HashSet;
import java.util.Set;

public class SeckillRecoveryCheckResult {
  private boolean startEventAvailable;
  private boolean finishEventAvailable;
  private int remainingCoupons;
  private Set<String> claimedCustomers;

  public boolean isStartEventAvailable() {
    return startEventAvailable;
  }

  public int remainingCoupons() {
    return remainingCoupons;
  }

  public void setStartEventAvailable(boolean startEventAvailable) {
    this.startEventAvailable = startEventAvailable;
  }

  public void setRemainingCoupons(int remainingCoupons) {
    this.remainingCoupons = remainingCoupons;
  }

  public boolean isFinishEventAvailable() {
    return finishEventAvailable;
  }

  public void setFinishEventAvailable(boolean finishEventAvailable) {
    this.finishEventAvailable = finishEventAvailable;
  }

  public Set<String> getClaimedCustomers() {
    return claimedCustomers;
  }

  public SeckillRecoveryCheckResult(int remainingCoupons) {
    startEventAvailable = false;
    finishEventAvailable = false;
    this.remainingCoupons = remainingCoupons;
    this.claimedCustomers = new HashSet<>();
  }
}
