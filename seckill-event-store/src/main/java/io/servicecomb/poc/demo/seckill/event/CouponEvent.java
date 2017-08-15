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

import io.servicecomb.poc.demo.seckill.CouponInfo;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CouponEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  private String couponId;

  //CouponEventType
  private String type;

  private Date time;

  private Integer count;

  private Float discount;

  private String customerId;

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

  public String getCustomerId() {
    return customerId;
  }

  public CouponEvent() { }

  public static CouponEvent genStartCouponEvent(CouponInfo info) {
    CouponEvent event = new CouponEvent();
    event.type = CouponEventType.Start;
    event.couponId = info.getId();
    event.time = new Date();
    event.count = info.getCount();
    event.discount = info.getDiscount();
    return event;
  }

  public static CouponEvent genFinishCouponEvent(CouponInfo info) {
    CouponEvent event = new CouponEvent();
    event.type = CouponEventType.Finish;
    event.couponId = info.getId();
    event.time = new Date();
    event.count = info.getCount();
    event.discount = info.getDiscount();
    return event;
  }

  public static CouponEvent genSecKillCouponEvent(CouponInfo info,String customerId) {
    CouponEvent event = new CouponEvent();
    event.type = CouponEventType.SecKill;
    event.couponId = info.getId();
    event.time = new Date();
    event.count = 1;
    event.discount = info.getDiscount();
    event.customerId = customerId;
    return event;
  }
}
