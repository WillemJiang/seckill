package io.servicecomb.poc.demo.seckill.repositories;

import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SpringBasedCouponEventRepository extends PagingAndSortingRepository<CouponEvent, Integer>, CouponEventRepository {
//  List<CouponEvent> findByCustomerId(String customerId);
//  List<CouponEvent> findByTypeNotOrderByTimeDesc(String type);

  CouponEvent findTopByCouponIdAndTypeOrderByIdDesc(String couponId,String type);
  List<CouponEvent> findByCouponIdAndIdGreaterThan(String couponId,int id);

}
