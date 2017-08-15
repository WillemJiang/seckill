package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import io.servicecomb.poc.demo.seckill.event.CouponEventType;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedCouponEventRepository;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SecKillController<T> {

  private SecKillCommandService secKillCommandService;
  private SecKillPersistentRunner secKillPersistentRunner;
  private CouponInfo couponInfo;
  private AtomicInteger claimedCoupons;
  private BlockingQueue<T> couponQueue = null;

  private final SpringBasedCouponEventRepository repository;

  public SecKillController(CouponInfo couponInfo, SpringBasedCouponEventRepository repository,SeckillRecoveryCheckResult recoveryInfo) {
    this.couponInfo = couponInfo;
    this.claimedCoupons = new AtomicInteger();
    this.couponQueue = new ArrayBlockingQueue<>(couponInfo.getCount());
    this.repository = repository;

    this.secKillCommandService = new SecKillCommandService(couponInfo, couponQueue, claimedCoupons, recoveryInfo);
    this.secKillPersistentRunner = new SecKillPersistentRunner(couponInfo, couponQueue, claimedCoupons,repository, recoveryInfo);
    this.secKillPersistentRunner.run();
  }

  public boolean seckill(T customerId) {
    return this.secKillCommandService.addCouponTo(customerId);
  }

  public void finish() {
    this.secKillPersistentRunner.finish();
  }
}
