package io.servicecomb.poc.demo.seckill.repositories;

import io.servicecomb.poc.demo.seckill.event.CouponEvent;

public interface CouponEventRepository {
  CouponEvent save(CouponEvent item);
}
