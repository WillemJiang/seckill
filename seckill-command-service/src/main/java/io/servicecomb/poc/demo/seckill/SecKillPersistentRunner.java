package io.servicecomb.poc.demo.seckill;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

class SecKillPersistentRunner<T> {

  private final BlockingQueue<T> coupons;
  private final CouponRepository<T> repository;

  SecKillPersistentRunner(BlockingQueue<T> coupons, CouponRepository<T> repository) {
    this.coupons = coupons;
    this.repository = repository;
  }

  void run() {
    CompletableFuture.runAsync(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          repository.save(new Coupon<>(coupons.take()));
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
  }
}
