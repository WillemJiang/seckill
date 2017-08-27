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

package io.servicecomb.poc.demo.seckill;

import java.util.Date;

public class Coupon<T> {
  private int id;

  private String couponId;

  private Date time;

  private Float discount;

  private T customerId;

  Coupon(int id, String couponId, Date time, Float discount, T customerId) {
    this.id = id;
    this.couponId = couponId;
    this.time = time;
    this.discount = discount;
    this.customerId = customerId;
  }

  public int getId() {
    return id;
  }

  public String getCouponId() {
    return couponId;
  }

  public Date getTime() {
    return time;
  }

  public Float getDiscount() {
    return discount;
  }

  public T getCustomerId() {
    return customerId;
  }
}
