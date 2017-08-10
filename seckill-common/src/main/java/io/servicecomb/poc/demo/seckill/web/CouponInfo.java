package io.servicecomb.poc.demo.seckill.web;

import java.util.Date;

public class CouponInfo {

  private String id;
  private Date time;
  private String customer_id;
  private int count;
  private float discount;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String getCustomer_id() {
    return customer_id;
  }

  public void setCustomer_id(String customer_id) {
    this.customer_id = customer_id;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public float getDiscount() {
    return discount;
  }

  public void setDiscount(float discount) {
    this.discount = discount;
  }

  public CouponInfo() {
  }

  public CouponInfo(String id, Date time, String customer_id, int count, float discount) {
    this.id = id;
    this.time = time;
    this.customer_id = customer_id;
    this.count = count;
    this.discount = discount;
  }
}