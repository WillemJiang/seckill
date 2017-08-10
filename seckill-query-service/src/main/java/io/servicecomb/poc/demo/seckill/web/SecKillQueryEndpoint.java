package io.servicecomb.poc.demo.seckill.web;

import java.util.List;

public interface SecKillQueryEndpoint {
  List<CouponInfo> querySuccess(String customerId);
  CouponInfo queryCurrent();
}
