package io.servicecomb.poc.demo.seckill;

class Coupon<T> {

  private final T customerId;

  //ticket discount
  private final double discount;

  Coupon(T customerId, double discount) {
    this.customerId = customerId;
    this.discount = discount;
  }

  T getCustomerId() {
    return customerId;
  }

  double getDiscount() {
    return discount;
  }
}
