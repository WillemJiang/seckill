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

package io.servicecomb.poc.demo.seckill.dto;

import java.util.Date;

public class PromotionDto {
  private int numberOfCoupons = 0;
  private float discount = 1;
  private Date publishTime;
  private Date finishTime;

  public PromotionDto() {
  }

  public PromotionDto(int numberOfCoupons, float discount, Date publishTime) {
    this(numberOfCoupons, discount, publishTime, new Date(Long.MAX_VALUE));
  }

  public PromotionDto(int numberOfCoupons, float discount, Date publishTime, Date finishTime) {
    this.numberOfCoupons = numberOfCoupons;
    this.discount = discount;
    this.publishTime = publishTime;
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
}
