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

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PromotionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  private String promotionId;
  private Date publishTime;
  private Date finishTime;
  private int numberOfCoupons;
  private float discount;

  public int getId() {
    return id;
  }

  public String getPromotionId() {
    return promotionId;
  }

  public void setPromotionId(String promotionId) {
    this.promotionId = promotionId;
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

  public int getNumberOfCoupons() {
    return numberOfCoupons;
  }

  public void setNumberOfCoupons(int numberOfCoupons) {
    this.numberOfCoupons = numberOfCoupons;
  }

  public float getDiscount() {
    return discount;
  }

  public void setDiscount(float discount) {
    this.discount = discount;
  }

  public PromotionEntity() {
  }

  public PromotionEntity(Date publishTime, int numberOfCoupons, float discount) {
    this(publishTime, new Date(Long.MAX_VALUE), numberOfCoupons, discount);
  }

  public PromotionEntity(Date publishTime, Date finishTime, int numberOfCoupons, float discount) {
    this.promotionId = UUID.randomUUID().toString();
    this.publishTime = publishTime;
    this.finishTime = finishTime;
    this.numberOfCoupons = numberOfCoupons;
    this.discount = discount;
  }

  @Override
  public String toString() {
    return "PromotionEntity{" +
        "promotionId='" + promotionId + '\'' +
        ", publishTime=" + publishTime +
        ", finishTime=" + finishTime +
        ", numberOfCoupons=" + numberOfCoupons +
        ", discount=" + discount +
        '}';
  }
}