package io.servicecomb.poc.demo.seckill.event;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CouponEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  //start : new coupon started
  //seckill : customer success kill a ticket
  //finish : coupon finish
  private String type;

  private Date startTime;

  private Integer count;

  private Float discount;

  //only when type = seckill
  private String customerId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Float getDiscount() {
    return discount;
  }

  public void setDiscount(Float discount) {
    this.discount = discount;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public CouponEvent() {
  }

  public CouponEvent(String type, String customerId, Float discount) {
    this.type = type;
    this.startTime = new Date(System.currentTimeMillis());
    this.discount = discount;
    this.customerId = customerId;
  }

  public CouponEvent(String type, Integer count, Float discount) {
    this.type = type;
    this.startTime = new Date(System.currentTimeMillis());
    this.count = count;
    this.discount = discount;
  }
}
