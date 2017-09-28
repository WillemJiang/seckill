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

import java.io.Serializable;
import java.util.Date;

public class CouponInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  private int id;

  private String promotionId;

  private Date time;

  private float discount;

  private String customerId;

  public CouponInfo() {
  }

  public CouponInfo(int id, String customerId, String promotionId, Date time, float discount) {
    this.id = id;
    this.customerId = customerId;
    this.promotionId = promotionId;
    this.time = time;
    this.discount = discount;
  }

  public int getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getPromotionId() {
    return promotionId;
  }

  public Date getTime() {
    return time;
  }

  public float getDiscount() {
    return discount;
  }
}
