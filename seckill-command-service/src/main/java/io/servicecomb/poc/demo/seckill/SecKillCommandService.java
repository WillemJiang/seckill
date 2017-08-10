package io.servicecomb.poc.demo.seckill;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class SecKillCommandService<T> {

  private final Queue<T> coupons;
  private final int totalCoupons;

  private final AtomicInteger claimedCoupons;

  public int getTotalCoupons() {
    return totalCoupons;
  }

  public SecKillCommandService(Queue<T> coupons,int totalCoupons) {
    this.coupons = coupons;
    this.totalCoupons = totalCoupons;
    this.claimedCoupons = new AtomicInteger(1);
  }

  public SecKillCode addCouponTo(T customerId) {
    //return muti states
    int value = claimedCoupons.getAndIncrement();
    if(value <= totalCoupons){
      boolean result = coupons.offer(customerId);
      if(result) {
        return value == totalCoupons ? SecKillCode.Finish : SecKillCode.Success;
      } else {
        return SecKillCode.Failed;
      }
    }
    return SecKillCode.Failed;
  }
}
