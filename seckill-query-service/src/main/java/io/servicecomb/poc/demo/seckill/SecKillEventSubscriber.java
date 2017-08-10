package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import io.servicecomb.poc.demo.seckill.event.CouponEventType;
import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import io.servicecomb.poc.demo.seckill.web.CouponInfo;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecKillEventSubscriber {

  @Autowired
  private CouponEventRepository repository;

  public List<CouponInfo> querySuccessCoupon(String customerId){
    List<CouponEvent> events = repository.findByCustomerId(customerId);
    return events.stream().map(
        event -> new CouponInfo(event.getId(), event.getTime(), event.getCustomerId(), event.getCount(),
            event.getDiscount())).collect(Collectors.toList());
  }

  public CouponInfo queryCurrentCoupon(){
    List<CouponEvent> events = repository.findByTypeNotOrderByTimeDesc(CouponEventType.SecKill);
    for (CouponEvent event : events) {
      if(event.getType().equals(CouponEventType.Start)) {
        if(events.stream().noneMatch(e -> e.getType().equals(CouponEventType.Finish) && e.getCustomerId().equals(event.getId()))) {
          return new CouponInfo(event.getId(), event.getTime(), event.getCustomerId(), event.getCount(),
              event.getDiscount());
        }
      }
    }
    return null;
  }
}
