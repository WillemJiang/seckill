package io.servicecomb.poc.demo.seckill.web;

import io.servicecomb.poc.demo.seckill.SecKillEventSubscriber;
import io.servicecomb.provider.rest.common.RestSchema;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//use restfull style
@RestSchema(schemaId = "query")
@RestController
@RequestMapping("/query")
public class SeckillQueryRESTEndpoint implements SecKillQueryEndpoint {

  private Logger logger = Logger.getLogger(SeckillQueryRESTEndpoint.class.getName());

  @Autowired
  private SecKillEventSubscriber secKillEventSubscriber;

  @Override
  @RequestMapping(method = RequestMethod.GET,value = "/success/{customerId}")
  public List<CouponInfo> querySuccess(@PathVariable String customerId) {
    return secKillEventSubscriber.querySuccessCoupon(customerId);
  }

  @Override
  @RequestMapping(method = RequestMethod.GET,value = "/current")
  public CouponInfo queryCurrent() {
    return secKillEventSubscriber.queryCurrentCoupon();
  }
}
