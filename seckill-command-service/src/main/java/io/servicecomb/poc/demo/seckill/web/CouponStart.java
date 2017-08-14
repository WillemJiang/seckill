package io.servicecomb.poc.demo.seckill.web;

public class CouponStart {

  private int number = 0;
  private float discount = 1;

  public CouponStart() { }

  public CouponStart(int number, float discount) {
    this.number = number;
    this.discount = discount;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public float getDiscount() {
    return discount;
  }

  public void setDiscount(float discount) {
    this.discount = discount;
  }
}
