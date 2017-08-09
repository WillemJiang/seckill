package io.servicecomb.poc.demo.seckill;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

class SecKillPersistentRunner<T> {

  private final BlockingQueue<Coupon<T>> coupons;
  private final CouponRepository<T> repository;

  SecKillPersistentRunner(BlockingQueue<Coupon<T>> coupons, CouponRepository<T> repository) {
    this.coupons = coupons;
    this.repository = repository;
  }

  void run() {
    CompletableFuture.runAsync(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          repository.save(coupons.take());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
  }
}
