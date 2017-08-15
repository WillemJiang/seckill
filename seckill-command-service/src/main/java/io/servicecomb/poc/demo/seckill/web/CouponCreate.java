package io.servicecomb.poc.demo.seckill.web;

import java.util.Date;

public class CouponCreate {

  private int number = 0;
  private float discount = 1;
  private Date publishTime;

  public CouponCreate() { }

  public CouponCreate(int number, float discount,Date publishTime) {
    this.number = number;
    this.discount = discount;
    this.publishTime = publishTime;
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

  public Date getPublishTime() {
    return publishTime;
  }

  public void setPublishTime(Date publishTime) {
    this.publishTime = publishTime;
  }
}
