package io.servicecomb.poc.demo.seckill;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class SecKillCommandService<T> {

  private final Queue<T> couponQueue;
  private final AtomicInteger claimedCoupons;
  private final CouponInfo couponInfo;
  private final SeckillRecoveryCheckResult recoveryInfo;


  public SecKillCommandService(CouponInfo couponInfo,Queue<T> couponQueue,AtomicInteger claimedCoupons,SeckillRecoveryCheckResult recoveryInfo) {
    this.couponInfo = couponInfo;
    this.couponQueue = couponQueue;
    this.claimedCoupons = claimedCoupons;
    this.recoveryInfo = recoveryInfo;
  }

  public boolean addCouponTo(T customerId) {
    if(recoveryInfo.getClaimedCustomers().add(customerId.toString())) {
      int value = claimedCoupons.getAndIncrement();
      if (value < couponInfo.getCount()) {
        return couponQueue.offer(customerId);
      }
    }
    return false;
  }
}
