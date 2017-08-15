package io.servicecomb.poc.demo.seckill.repositories;

import io.servicecomb.poc.demo.seckill.CouponInfo;
import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CouponInfoRepository extends CrudRepository<CouponInfo, String> {
}
