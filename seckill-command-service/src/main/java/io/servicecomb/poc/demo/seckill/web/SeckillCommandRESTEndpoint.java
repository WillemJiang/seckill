package io.servicecomb.poc.demo.seckill.web;

import io.servicecomb.poc.demo.seckill.SecKillCode;
import io.servicecomb.poc.demo.seckill.SecKillController;
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

  private Logger logger = Logger.getLogger(SeckillCommandRESTEndpoint.class.getName());

  @Autowired
  private SecKillController secKillController;

  @Override
  @RequestMapping(method = RequestMethod.POST,value = "/seckill")
  public boolean seckill(
      @RequestBody CouponSecKill secKill) {
    if(secKill.getId() != null && !secKill.getId().isEmpty()) {
      SecKillCode result = secKillController.seckill(secKill.getId());
      logger.info(String.format("seckill from = %s result = %s", secKill.getId(), result));
      return result != SecKillCode.Failed;
    } else {
      return false;
    }
  }
}
