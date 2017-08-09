package io.servicecomb.poc.demo.seckill.repositories;

import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CouponEventRepository extends CrudRepository<CouponEvent, Long> {
  List<CouponEvent> findByCustomerId(String customerId);
}
