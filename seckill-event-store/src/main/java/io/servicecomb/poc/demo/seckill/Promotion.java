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
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Promotion {

  @Id
  private String id;
  private Date publishTime;
  private Date finishTime;
  private int numberOfCoupons;
  private float discount;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public Promotion() {
  }

  public Promotion(Date publishTime,int numberOfCoupons, float discount) {
    this(publishTime,new Date(Long.MAX_VALUE), numberOfCoupons,discount);
  }

  public Promotion(Date publishTime,Date finishTime, int numberOfCoupons, float discount) {
    this.id = UUID.randomUUID().toString();
    this.publishTime = publishTime;
    this.finishTime = finishTime;
    this.numberOfCoupons = numberOfCoupons;
    this.discount = discount;
  }
}