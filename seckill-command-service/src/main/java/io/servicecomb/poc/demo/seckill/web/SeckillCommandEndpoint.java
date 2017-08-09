package io.servicecomb.poc.demo.seckill.web;

public interface SeckillCommandEndpoint {

  //star a seckill
  boolean start(int number,float discount);
  //request a seckill
  boolean seckill(String customerId);
}
