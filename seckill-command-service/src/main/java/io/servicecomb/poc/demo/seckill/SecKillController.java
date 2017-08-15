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

import io.servicecomb.poc.demo.seckill.repositories.SpringBasedCouponEventRepository;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SecKillController<T> {

  private SecKillCommandService secKillCommandService;
  private SecKillPersistentRunner secKillPersistentRunner;
  private CouponInfo couponInfo;
  private AtomicInteger claimedCoupons;
  private BlockingQueue<T> couponQueue = null;

  private final SpringBasedCouponEventRepository repository;

  public SecKillController(CouponInfo couponInfo, SpringBasedCouponEventRepository repository,SeckillRecoveryCheckResult recoveryInfo) {
    this.couponInfo = couponInfo;
    this.claimedCoupons = new AtomicInteger();
    this.couponQueue = new ArrayBlockingQueue<>(couponInfo.getCount());
    this.repository = repository;

    this.secKillCommandService = new SecKillCommandService(couponInfo, couponQueue, claimedCoupons, recoveryInfo);
    this.secKillPersistentRunner = new SecKillPersistentRunner(couponInfo, couponQueue, claimedCoupons,repository, recoveryInfo);
    this.secKillPersistentRunner.run();
  }

  public boolean seckill(T customerId) {
    return this.secKillCommandService.addCouponTo(customerId);
  }

  public void finish() {
    this.secKillPersistentRunner.finish();
  }
}
