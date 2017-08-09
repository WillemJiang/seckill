package io.servicecomb.poc.demo.seckill.web;

import io.servicecomb.poc.demo.seckill.Coupon;
import io.servicecomb.poc.demo.seckill.SecKillCommandService;
import io.servicecomb.poc.demo.seckill.SecKillPersistentRunner;
import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import io.servicecomb.poc.demo.seckill.event.CouponEventType;
import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import io.servicecomb.provider.rest.common.RestSchema;
import io.swagger.annotations.Api;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;

//use restfull style
@RestSchema(schemaId = "seckillCommand")
@Path("/command")
@Api(value = "Coupon Command Service", produces = MediaType.APPLICATION_JSON)
public class SeckillCommandRESTEndpoint implements SeckillCommandEndpoint {

  private Logger logger = Logger.getLogger(SeckillCommandRESTEndpoint.class.getName());

  @Autowired
  private CouponEventRepository couponRepository;

  private final Object lock = new Object();
  private BlockingQueue<String> coupons = null;
  private SecKillCommandService<String> commandService = null;
  private SecKillPersistentRunner<String> persistentRunner = null;

  @Override
  @GET
  @Path("/start/{number}/{discount}")
  @Produces(MediaType.APPLICATION_JSON)
  public boolean start(
      @PathParam("number") int number,
      @PathParam("discount") float discount) {
    if (number > 0 && discount > 0 && discount <= 1) {
      CouponEvent event;
      synchronized (lock) {
        coupons = new ArrayBlockingQueue<>(number);
        commandService = new SecKillCommandService<>(coupons, number);
        persistentRunner = new SecKillPersistentRunner<>(coupons, discount, couponRepository);
        event = new CouponEvent(CouponEventType.Start, number, discount);
      }
      couponRepository.save(event);
      persistentRunner.run();
    }
    logger.info(String.format("star a new coupon number = %d discount = %f", number, discount));
    return true;
  }


  @Override
  @GET
  @Path("/seckill/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public boolean seckill(
      @PathParam("id") String customerId) {
    SecKillCommandService<String> service;
    synchronized (lock) {
      service = commandService;
    }
    boolean result = service.addCouponTo(customerId);
    logger.info(String.format("seckill from = %s result = %s", customerId, result));
    return result;
  }
}
