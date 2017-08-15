package io.servicecomb.poc.demo.seckill;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import io.servicecomb.poc.demo.seckill.event.CouponEventType;
import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

public class SecKillPersistentRunnerTest {

  private final int numberOfCoupons = 5;

  private final List<String> customerIds = new LinkedList<>();
  private AtomicBoolean isPromotionEnded = new AtomicBoolean(false);
  private final CouponEventRepository repository = couponEvent -> {
    if (CouponEventType.Finish.equals(couponEvent.getType())) {
      isPromotionEnded.set(true);
    } else if (CouponEventType.Start.equals(couponEvent.getType())) {
    } else {
      customerIds.add(couponEvent.getCustomerId());
    }
    return couponEvent;
  };

  private final BlockingQueue<String> coupons = new ArrayBlockingQueue<>(numberOfCoupons);
  private final AtomicInteger claimedCoupons = new AtomicInteger();

  @Test
  public void persistsCouponUsingRepo() {
    CouponInfo couponInfo = new CouponInfo(new Date(), numberOfCoupons, 0.7f);
    SeckillRecoveryCheckResult recovery = new SeckillRecoveryCheckResult(numberOfCoupons);
    SecKillPersistentRunner<String> runner = new SecKillPersistentRunner<>(couponInfo, coupons,claimedCoupons, repository, recovery);

    for (int i = 0; i < numberOfCoupons; i++) {
      coupons.offer(String.valueOf(i));
    }
    claimedCoupons.set(numberOfCoupons);
    runner.run();
    waitAtMost(2, SECONDS).until(coupons::isEmpty);
    assertThat(customerIds, contains("0", "1", "2", "3", "4"));

    assertThat(isPromotionEnded.get(), is(true));
  }

  @Test
  public void exitsWhenFinishTimeReach() throws InterruptedException {
    int delaySeconds = 3;
    Date start = new Date();
    CouponInfo couponInfo = new CouponInfo(start, DateUtils.addSeconds(start,delaySeconds), numberOfCoupons, 0.7f);
    SeckillRecoveryCheckResult recovery = new SeckillRecoveryCheckResult(numberOfCoupons);
    SecKillPersistentRunner<String> runner = new SecKillPersistentRunner<>(couponInfo, coupons,claimedCoupons, repository, recovery);

    for (int i = 0; i < numberOfCoupons - 1; i++) {
      coupons.offer(String.valueOf(i));
    }
    claimedCoupons.set(numberOfCoupons - 1);
    runner.run();

    waitAtMost(delaySeconds * 2, SECONDS).untilTrue(isPromotionEnded);

    assertThat(customerIds, contains("0", "1", "2", "3"));

    assertThat(isPromotionEnded.get(), is(true));
  }


  @Test
  public void exitsWhenAllCouponsConsumed() throws Exception {
    CouponInfo couponInfo = new CouponInfo(new Date(), numberOfCoupons, 0.7f);
    SeckillRecoveryCheckResult recovery = new SeckillRecoveryCheckResult(numberOfCoupons);
    SecKillPersistentRunner<String> runner = new SecKillPersistentRunner<>(couponInfo, coupons,claimedCoupons, repository, recovery);
    runner.run();

    Thread.sleep(3000);

    for (int i = 0; i < numberOfCoupons; i++) {
      coupons.offer(String.valueOf(i));
    }
    claimedCoupons.set(numberOfCoupons);


    waitAtMost(2, SECONDS).until(coupons::isEmpty);
    assertThat(customerIds, contains("0", "1", "2", "3", "4"));

    assertThat(isPromotionEnded.get(), is(true));
  }
}
