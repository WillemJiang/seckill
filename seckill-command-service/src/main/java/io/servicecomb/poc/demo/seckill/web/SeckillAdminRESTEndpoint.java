package io.servicecomb.poc.demo.seckill.web;

import io.servicecomb.poc.demo.seckill.SecKillController;
import io.servicecomb.provider.rest.common.RestSchema;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//use restfull style
@RestSchema(schemaId = "admin")
@RestController
@RequestMapping("/admin")
public class SeckillAdminRESTEndpoint implements SecKillAdminEndpoint {

  private Logger logger = Logger.getLogger(SeckillAdminRESTEndpoint.class.getName());

  @Autowired
  private SecKillController secKillController;

  @Override
  @RequestMapping(method = RequestMethod.POST, value = "/start")
  public boolean start(
      @RequestBody CouponStart start) {
    if (start.getNumber() > 0 && start.getDiscount() > 0 && start.getDiscount() <= 1) {
      secKillController.create(start.getNumber(), start.getDiscount());
      logger.info(String.format("star a new coupon number = %d discount = %f", start.getNumber(), start.getDiscount()));
      return true;
    } else {
      return false;
    }
  }
}
