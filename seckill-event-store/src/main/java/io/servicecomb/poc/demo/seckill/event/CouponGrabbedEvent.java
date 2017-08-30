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

import io.servicecomb.poc.demo.seckill.Coupon;
import io.servicecomb.poc.demo.seckill.entities.PromotionEntity;
import io.servicecomb.poc.demo.seckill.json.ToJsonFormat;

public class CouponGrabbedEvent<T> extends SecKillEvent {

  protected Coupon<T> coupon;

  public CouponGrabbedEvent() {
    super();
    this.type = CouponGrabbedEvent.class.getSimpleName();
  }

  public CouponGrabbedEvent(Coupon<T> coupon) {
    this();
    this.promotionId = coupon.getPromotionId();
    this.coupon = coupon;
  }

  public CouponGrabbedEvent(PromotionEntity promotion, T customerId) {
    this();
    this.promotionId = promotion.getPromotionId();
    this.coupon = new Coupon<>(promotion.getPromotionId(), System.currentTimeMillis(), promotion.getDiscount(),
        customerId);
  }

  public Coupon<T> getCoupon() {
    return coupon;
  }

  @Override
  public String json(ToJsonFormat toJsonFormat) {
    return toJsonFormat.toJson(coupon);
  }
}
