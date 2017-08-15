package io.servicecomb.poc.demo.seckill.web;

public class CouponStart {

  private String couponId;

  public CouponStart() { }

  public CouponStart(String couponId) {
    this.couponId = couponId;
  }

  public String getCouponId() {
    return couponId;
  }

  public void setCouponId(String couponId) {
    this.couponId = couponId;
  }
}
