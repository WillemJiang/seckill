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
import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SecKillPersistentRunner<T> {

  private final BlockingQueue<T> coupons;
  private final CouponEventRepository repository;
  private final AtomicInteger claimedCoupons;
  private final CouponInfo couponInfo;

  private final SeckillRecoveryCheckResult recoveryInfo;

  private CompletableFuture<Void> future;

  public SecKillPersistentRunner(CouponInfo couponInfo, BlockingQueue<T> couponQueue, AtomicInteger claimedCoupons,
      CouponEventRepository repository, SeckillRecoveryCheckResult recoveryInfo) {
    this.couponInfo = couponInfo;
    this.coupons = couponQueue;
    this.repository = repository;
    this.claimedCoupons = claimedCoupons;
    this.recoveryInfo = recoveryInfo;
  }

  public void run() {
    if (!recoveryInfo.isStartEventAvailable()) {
      CouponEvent start = CouponEvent.genStartCouponEvent(couponInfo);
      repository.save(start);
    }

    future = CompletableFuture.runAsync(() -> {

      while (!Thread.currentThread().isInterrupted() && (claimedCoupons.get() < recoveryInfo.getLeftCount()
          || coupons.size() != 0) && couponInfo.getFinishTime().getTime() > System.currentTimeMillis()) {
        try {
          long leftPoolTime = couponInfo.getFinishTime().getTime() - System.currentTimeMillis();
          if (leftPoolTime > 0) {
            T customerId = coupons.poll(leftPoolTime, TimeUnit.MILLISECONDS);
            CouponEvent event = CouponEvent.genSecKillCouponEvent(couponInfo, customerId.toString());
            repository.save(event);
          } else {
            break;
          }

        } catch (Exception e) {
          Thread.currentThread().interrupt();
        }
      }
      CouponEvent finish = CouponEvent.genFinishCouponEvent(couponInfo);
      repository.save(finish);
    });
  }

  public void finish() {
    future.cancel(true);
  }
}
