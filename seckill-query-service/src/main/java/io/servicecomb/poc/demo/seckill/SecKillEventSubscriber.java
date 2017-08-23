package io.servicecomb.poc.demo.seckill;

import java.util.List;

public class SecKillEventSubscriber {

  SeckillEventLoader seckillEventLoader;

  public SecKillEventSubscriber(SeckillEventLoader seckillEventLoader){
    this.seckillEventLoader = seckillEventLoader;
  }

  public List<Coupon> querySuccessCoupon(String customerId){
    return seckillEventLoader.getCustomerCoupons(customerId);
  }

  public List<Promotion> queryCurrentPromotion(){
    return seckillEventLoader.getActivePromotions();
  }
}
