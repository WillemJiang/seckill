package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import io.servicecomb.poc.demo.seckill.event.CouponEventType;
import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public class SecKillPersistentRunner<T> {

  private final BlockingQueue<T> coupons;

  private final CouponEventRepository repository;

  private final float discount;

  public SecKillPersistentRunner(BlockingQueue<T> coupons,float discount, CouponEventRepository repository) {
    this.coupons = coupons;
    this.discount = discount;
    this.repository = repository;
  }

  public void run() {
    CompletableFuture.runAsync(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          CouponEvent event = new CouponEvent(CouponEventType.SecKill,coupons.take().toString(),discount);
          repository.save(event);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
  }
}
