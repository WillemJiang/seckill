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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class SecKillPersistentRunnerTest {

  private final int numberOfCoupons = 5;

  private final List<String> customerIds = new LinkedList<>();
  private volatile boolean isPromotionEnded;
  private final CouponEventRepository repository = couponEvent -> {
    if (PromotionEventType.Finish.equals(couponEvent.getType())) {
      isPromotionEnded = true;
    } else if (PromotionEventType.Grab.equals(couponEvent.getType())) {
      customerIds.add(couponEvent.getCustomerId());
    }
    return couponEvent;
  };

  private final BlockingQueue<String> coupons = new ArrayBlockingQueue<>(numberOfCoupons);
  private final AtomicInteger claimedCoupons = new AtomicInteger();

  @Test
  public void persistsCouponUsingRepo() {
    Promotion promotion = new Promotion(new Date(), numberOfCoupons, 0.7f);
    SeckillRecoveryCheckResult recovery = new SeckillRecoveryCheckResult(numberOfCoupons);
    SecKillPersistentRunner<String> runner = new SecKillPersistentRunner<>(promotion, coupons, claimedCoupons,
        repository, recovery);

    for (int i = 0; i < numberOfCoupons; i++) {
      coupons.offer(String.valueOf(i));
    }
    claimedCoupons.set(numberOfCoupons);
    runner.run();
    waitAtMost(2, SECONDS).until(coupons::isEmpty);
    assertThat(customerIds, contains("0", "1", "2", "3", "4"));

    assertThat(isPromotionEnded, is(true));
  }

  @Test
  public void exitsWhenFinishTimeReach() throws InterruptedException {
    int delaySeconds = 1;
    ZonedDateTime publishTime = ZonedDateTime.now();

    Promotion promotion = new Promotion(dateOf(publishTime), dateOf(publishTime.plusSeconds(delaySeconds)), numberOfCoupons, 0.7f);
    SeckillRecoveryCheckResult recovery = new SeckillRecoveryCheckResult(numberOfCoupons);
    SecKillPersistentRunner<String> runner = new SecKillPersistentRunner<>(promotion, coupons, claimedCoupons,
        repository, recovery);

    coupons.offer(String.valueOf(0));
    runner.run();

    waitAtMost(delaySeconds * 2, SECONDS).until(() -> isPromotionEnded);

    assertThat(customerIds, contains("0"));
  }

  @Test
  public void exitsWhenAllCouponsConsumed() throws Exception {
    Promotion promotion = new Promotion(new Date(), numberOfCoupons, 0.7f);
    SeckillRecoveryCheckResult recovery = new SeckillRecoveryCheckResult(numberOfCoupons);
    SecKillPersistentRunner<String> runner = new SecKillPersistentRunner<>(promotion, coupons, claimedCoupons,
        repository, recovery);
    runner.run();

    Thread.sleep(1000);

    for (int i = 0; i < numberOfCoupons; i++) {
      coupons.offer(String.valueOf(i));
    }
    claimedCoupons.set(numberOfCoupons);

    waitAtMost(2, SECONDS).until(coupons::isEmpty);
    assertThat(customerIds, contains("0", "1", "2", "3", "4"));

    assertThat(isPromotionEnded, is(true));
  }

  private Date dateOf(ZonedDateTime publishTime) {
    return Date.from(publishTime.toInstant());
  }
}
