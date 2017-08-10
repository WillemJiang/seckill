package io.servicecomb.poc.demo.seckill.web;

import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement(name = "Coupon")
public class CouponInfo {

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "time")
  private Date time;

  @XmlElement(name = "customer_id")
  private String customer_id;

  @XmlElement(name = "count")
  private int count;

  @XmlElement(name = "discount")
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

  public CouponInfo(CouponEvent event) {
    this.id = event.getId();
    this.time = event.getTime();
    this.customer_id = event.getCustomerId();
    this.count = event.getCount();
    this.discount = event.getDiscount();
  }
}