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

  protected String promotionId;

  //PromotionEventType
  protected String type;

  protected Date time;

  protected Float discount;

  @Type(type = "java.lang.String")
  protected T customerId;

  public int getId() {
    return id;
  }

  public String getPromotionId() {
    return promotionId;
  }

  public String getType() {
    return type;
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

  public PromotionEvent() {
    this.time = new Date();
  }
}
