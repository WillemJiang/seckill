package io.servicecomb.poc.demo.seckill;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class SecKillCommandService<T> {

  private final Queue<T> coupons;

  private final String couponId;
  private final int totalCoupons;
  private final float discount;


  private final AtomicInteger claimedCoupons;

  public int getTotalCoupons() {
    return totalCoupons;
  }

  public String getCouponId() {
    return couponId;
  }

  public float getDiscount() {
    return discount;
  }

  public SecKillCommandService(Queue<T> coupons,String couponId, int totalCoupons,float discount) {
    this.coupons = coupons;
    this.couponId = couponId;
    this.totalCoupons = totalCoupons;
    this.discount = discount;
    this.claimedCoupons = new AtomicInteger(1);
  }

  public SecKillCode addCouponTo(T customerId) {
//    while (claimedCoupons.get() < totalCoupons) {
//      int oldVal = claimedCoupons.get();
//      int newVal = oldVal + 1;
//      if (claimedCoupons.compareAndSet(oldVal, newVal)) {
//        return coupons.offer(customerId);
//      }
//    }
//    return false;

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
