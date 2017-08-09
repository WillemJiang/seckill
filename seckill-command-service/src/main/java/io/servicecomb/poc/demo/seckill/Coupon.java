package io.servicecomb.poc.demo.seckill;

public class Coupon<T> {

  private final T customerId;

  //ticket discount
  private final float discount;

  public Coupon(T customerId, float discount) {
    this.customerId = customerId;
    this.discount = discount;
  }

  public T getCustomerId() {
    return customerId;
  }

  public float getDiscount() {
    return discount;
  }
}
