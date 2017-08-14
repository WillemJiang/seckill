package io.servicecomb.poc.demo.seckill.web;

public interface SeckillCommandEndpoint {
  //request a seckill
  boolean seckill(CouponSecKill secKill);
}
