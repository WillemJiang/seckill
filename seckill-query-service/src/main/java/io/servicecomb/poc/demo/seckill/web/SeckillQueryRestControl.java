package io.servicecomb.poc.demo.seckill.web;

import io.servicecomb.poc.demo.seckill.Coupon;
import io.servicecomb.poc.demo.seckill.Promotion;
import io.servicecomb.poc.demo.seckill.SecKillEventSubscriber;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/query")
public class SeckillQueryRestControl {

  private Logger logger = LoggerFactory.getLogger(SeckillQueryRestControl.class);

  @Autowired
  private SecKillEventSubscriber secKillEventSubscriber;


  @RequestMapping(method = RequestMethod.GET,value = "/coupons/{customerId}")
  public List<Coupon> querySuccess(@PathVariable String customerId) {
    logger.trace("Get request /query/coupons/%s",customerId);
    return secKillEventSubscriber.querySuccessCoupon(customerId);
  }

  @RequestMapping(method = RequestMethod.GET,value = "/promotions")
  public List<Promotion> queryCurrent() {
    logger.trace("Get request /query/promotions");
    return secKillEventSubscriber.queryCurrentPromotion();
  }
}
