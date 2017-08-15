package io.servicecomb.poc.demo.seckill.web;

public class CouponSecKill<T> {

  private T customerId;

  public T getCustomerId() {
    return customerId;
  }

  public void setCustomerId(T customerId) {
    this.customerId = customerId;
  }

  public CouponSecKill() { }

  public CouponSecKill(T id) {
    this.customerId = id;
  }
}
