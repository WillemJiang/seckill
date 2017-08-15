package io.servicecomb.poc.demo.seckill;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CouponInfo {

  @Id
  private String id;
  private Date createTime;
  private Date publishTime;
  private Date finishTime;
  private int count;
  private float discount;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getPublishTime() {
    return publishTime;
  }

  public void setPublishTime(Date publishTime) {
    this.publishTime = publishTime;
  }

  public Date getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(Date finishTime) {
    this.finishTime = finishTime;
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

  public CouponInfo(Date publishTime,int count, float discount) {
    this(publishTime,new Date(Long.MAX_VALUE),count,discount);
  }

  public CouponInfo(Date publishTime,Date finishTime, int count, float discount) {
    this.id = UUID.randomUUID().toString();
    this.createTime = new Date();
    this.publishTime = publishTime;
    this.finishTime = finishTime;
    this.count = count;
    this.discount = discount;
  }
}