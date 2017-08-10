package io.servicecomb.poc.demo.seckill.repositories;

import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponEventRepository extends JpaRepository<CouponEvent, String> {
  List<CouponEvent> findByCustomerId(String customerId);
  List<CouponEvent> findByTypeNotOrderByTimeDesc(String type);
}
