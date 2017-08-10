package io.servicecomb.poc.demo.seckill.event;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CouponEvent {

  @Id
  private String id;

  //CouponEventType
  private String type;

  private Date time;

  private Integer count;

  private Float discount;

  private String customerId;

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public Date getTime() {
    return time;
  }

  public Integer getCount() {
    return count;
  }

  public Float getDiscount() {
    return discount;
  }

  public String getCustomerId() {
    return customerId;
  }

  public CouponEvent() { }

  public CouponEvent(String type, String customerId, Float discount) {
    this.id = UUID.randomUUID().toString();
    this.type = type;
    this.count = 1;
    this.time = new Date(System.currentTimeMillis());
    this.discount = discount;
    this.customerId = customerId;
  }

  public CouponEvent(String type, Integer count, Float discount) {
    this.id = UUID.randomUUID().toString();
    this.type = type;
    this.time = new Date(System.currentTimeMillis());
    this.count = count;
    this.discount = discount;
  }

  public CouponEvent(String type, String customerId, Integer count, Float discount) {
    this(type, count, discount);
    this.customerId = customerId;
  }
}
