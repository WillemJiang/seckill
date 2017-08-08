package io.servicecomb.poc.demo.seckill;

class Coupon<T> {

  private final T customerId;

  Coupon(T customerId) {
    this.customerId = customerId;
  }

  T getCustomerId() {
    return customerId;
  }
}
