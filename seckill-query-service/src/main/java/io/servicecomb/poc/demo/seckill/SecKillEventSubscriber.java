package io.servicecomb.poc.demo.seckill;

import java.util.List;

public class SecKillEventSubscriber {

/*
  @Autowired
  private SeckillEventLoader seckillEventLoader;
*/

  SeckillEventLoader seckillEventLoader;

  public SecKillEventSubscriber(SeckillEventLoader seckillEventLoader){
    this.seckillEventLoader = seckillEventLoader;
  }

  public List<Coupon> querySuccessCoupon(String customerId){
    return (List<Coupon>) seckillEventLoader.getCustomerCoupons(customerId);
  }

  public List<Promotion> queryCurrentPromotion(){
    return (List<Promotion>) seckillEventLoader.getActivePromotions();
  }
}
