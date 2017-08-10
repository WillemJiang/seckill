package io.servicecomb.poc.demo.seckill.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CouponStart {

  private int number = 0;
  private float discount = 1;

  public CouponStart() { }

  public CouponStart(int number, float discount) {
    this.number = number;
    this.discount = discount;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public float getDiscount() {
    return discount;
  }

  public void setDiscount(float discount) {
    this.discount = discount;
  }

  @Override
  public String toString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return null;
    }
  }
}
