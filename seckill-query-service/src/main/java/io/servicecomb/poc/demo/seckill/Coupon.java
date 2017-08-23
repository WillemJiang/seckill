package io.servicecomb.poc.demo.seckill;

import java.util.Date;

public class Coupon<T> {
  private int id;

  private String couponId;

  private Date time;

  private Float discount;

  private T customerId;

  public Coupon(int id, String couponId, Date time, Float discount, T customerId) {
    this.id = id;
    this.couponId = couponId;
    this.time = time;
    this.discount = discount;
    this.customerId = customerId;
  }

  public int getId() {
    return id;
  }

  public String getCouponId() {
    return couponId;
  }

  public Date getTime() {
    return time;
  }

  public Float getDiscount() {
    return discount;
  }

  public T getCustomerId() {
    return customerId;
  }
}
