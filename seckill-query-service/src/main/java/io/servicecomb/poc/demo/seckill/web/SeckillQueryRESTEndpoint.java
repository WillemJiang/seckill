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

//use restfull style
@RestSchema(schemaId = "seckillQuery")
@Path("/query")
@Api(value = "Coupon Query Service", produces = MediaType.APPLICATION_JSON)
public class SeckillQueryRESTEndpoint implements SecKillQueryEndpoint {

  private Logger logger = Logger.getLogger(SeckillQueryRESTEndpoint.class.getName());

  @Autowired
  private SecKillEventSubscriber secKillEventSubscriber;

  @Override
  @GET
  @Path("/success/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<CouponInfo> querySuccess(@PathParam("id") String customerId) {
    return secKillEventSubscriber.querySuccessCoupon(customerId);
  }

  @Override
  @GET
  @Path("/current")
  @Produces(MediaType.APPLICATION_JSON)
  public CouponInfo queryCurrent() {
    return secKillEventSubscriber.queryCurrentCoupon();
  }
}
