package io.servicecomb.poc.demo.seckill;

interface CouponRepository<T> {

  void save(Coupon<T> coupon);
}
