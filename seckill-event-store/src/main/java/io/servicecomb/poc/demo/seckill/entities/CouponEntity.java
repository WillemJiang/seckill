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

package io.servicecomb.poc.demo.seckill.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.Type;

@Entity
public class CouponEntity<T> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  private String promotionId;

  private long time;

  private float discount;

  @Type(type = "java.lang.String")
  private T customerId;

  public CouponEntity() {
  }

  public CouponEntity(String promotionId, long time, float discount, T customerId) {
    this.promotionId = promotionId;
    this.time = time;
    this.discount = discount;
    this.customerId = customerId;
  }

  public int getId() {
    return id;
  }

  public String getPromotionId() {
    return promotionId;
  }

  public void setPromotionId(String promotionId) {
    this.promotionId = promotionId;
  }

  public T getCustomerId() {
    return customerId;
  }

  public void setCustomerId(T customerId) {
    this.customerId = customerId;
  }
}
