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
