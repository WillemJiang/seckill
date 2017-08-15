package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import io.servicecomb.poc.demo.seckill.event.CouponEventType;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedCouponEventRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecKillManager<T> {
  private SecKillController secKillController = null;

  @Autowired
  private SpringBasedCouponEventRepository repository;

  public synchronized SeckillRecoveryCheckResult recoveryCheck(CouponInfo info) {
    SeckillRecoveryCheckResult result = new SeckillRecoveryCheckResult(info.getCount());
    CouponEvent lastStart = this.repository.findTopByCouponIdAndTypeOrderByIdDesc(info.getId(),
        CouponEventType.Start);
    if (lastStart != null) {
      result.setStartEventAvailable(true);
      List<CouponEvent> allEvents = this.repository.findByCouponIdAndIdGreaterThan(info.getId(), lastStart.getId());
      if (allEvents.size() != 0) {
        result.setFinishEventAvailable(allEvents.stream().anyMatch(event -> CouponEventType.Finish.equals(event.getType())));
        long count = allEvents.stream().map(event -> CouponEventType.SecKill.equals(event.getType())).count();

        result.setLeftCount(lastStart.getCount() - (int) count);
        result.getClaimedCustomers().addAll(allEvents.stream().filter(event -> !CouponEventType.Finish.equals(event.getType()))
            .map(event -> event.getCustomerId()).distinct().collect(Collectors.toSet()));
      }
    }
    return result;
  }

  public synchronized boolean start(CouponInfo info) {
    SeckillRecoveryCheckResult result = recoveryCheck(info);
    if(!result.isFinishEventAvailable()) {
      if (secKillController != null)
        secKillController.finish();
      secKillController = new SecKillController(info, repository,result);
      return true;
    }
    return false;
  }

  public boolean seckill(T customerId) {
    return secKillController != null && secKillController.seckill(customerId);
  }
}
