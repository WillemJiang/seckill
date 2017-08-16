/*
 *   Copyright 2017 Huawei Technologies Co., Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.servicecomb.poc.demo.seckill.event;

import io.servicecomb.poc.demo.seckill.Promotion;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.Type;

@Entity
public class PromotionEvent<T> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  private String couponId;

  //PromotionEventType
  private String type;

  private Date time;

  private Integer count;

  private Float discount;

  @Type(type = "java.lang.String")
  private T customerId;

  public int getId() {
    return id;
  }

  public String getCouponId() {
    return couponId;
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

  public T getCustomerId() {
    return customerId;
  }

  public PromotionEvent() { }

  public static PromotionEvent<String> genStartCouponEvent(Promotion info) {
    PromotionEvent<String> event = new PromotionEvent<>();
    event.type = PromotionEventType.Start;
    event.couponId = info.getId();
    event.time = new Date();
    event.count = info.getNumberOfCoupons();
    event.discount = info.getDiscount();
    return event;
  }

  public static PromotionEvent<String> genFinishCouponEvent(Promotion info) {
    PromotionEvent<String> event = new PromotionEvent<>();
    event.type = PromotionEventType.Finish;
    event.couponId = info.getId();
    event.time = new Date();
    event.count = info.getNumberOfCoupons();
    event.discount = info.getDiscount();
    return event;
  }

  public static PromotionEvent<String> genSecKillCouponEvent(Promotion info,String customerId) {
    PromotionEvent<String> event = new PromotionEvent<>();
    event.type = PromotionEventType.SecKill;
    event.couponId = info.getId();
    event.time = new Date();
    event.count = 1;
    event.discount = info.getDiscount();
    event.customerId = customerId;
    return event;
  }
}
