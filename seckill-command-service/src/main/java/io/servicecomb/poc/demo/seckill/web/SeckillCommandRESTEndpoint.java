package io.servicecomb.poc.demo.seckill.web;

import io.servicecomb.poc.demo.seckill.SecKillManager;
import io.servicecomb.provider.rest.common.RestSchema;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//use restfull style
@RestSchema(schemaId = "command")
@RestController
@RequestMapping("/command")
public class SeckillCommandRESTEndpoint implements SeckillCommandEndpoint {

  private final Logger logger = Logger.getLogger(SeckillCommandRESTEndpoint.class.getName());

  @Autowired
  private SecKillManager secKillManager;

  @Override
  @RequestMapping(method = RequestMethod.POST,value = "/seckill")
  public boolean seckill(
      @RequestBody CouponSecKill secKill) {
    if(secKill.getCustomerId() != null) {
      boolean result = secKillManager.seckill(secKill.getCustomerId().toString());
      logger.info(String.format("seckill from = %s result = %s", secKill.getCustomerId(), result));
      return result;
    } else {
      return false;
    }
  }
}
