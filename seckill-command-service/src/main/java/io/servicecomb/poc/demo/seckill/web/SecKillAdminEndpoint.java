package io.servicecomb.poc.demo.seckill.web;

public interface SecKillAdminEndpoint {
  //star a seckill
  String create(CouponCreate create);
  boolean start(CouponStart start);
}
