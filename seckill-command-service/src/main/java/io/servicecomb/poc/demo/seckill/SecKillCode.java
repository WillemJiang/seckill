package io.servicecomb.poc.demo.seckill;

public enum SecKillCode {
  //成功
  Success(1),
  //最后一次成功（最后一张票）
  Finish(2),
  //失败
  Failed(3);

  private int index = 0;
  SecKillCode(int index) {
    this.index = index;
  }
}
