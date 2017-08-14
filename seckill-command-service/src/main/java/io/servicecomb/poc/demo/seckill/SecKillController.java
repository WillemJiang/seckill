package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import io.servicecomb.poc.demo.seckill.event.CouponEventType;
import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecKillController {

  private BlockingQueue<String> coupons = null;
  private SecKillCommandService<String> commandService = null;
  private CouponEvent currentEvent = null;
  private float discount;
  private SecKillPersistentRunner<String> persistentRunner = null;

  private boolean available = false;

  @Autowired
  private CouponEventRepository couponRepository;

  public synchronized boolean create(int number,float discount){
    if(!available) {
      this.discount = discount;
      this.currentEvent = new CouponEvent(CouponEventType.Start, number, discount);
      this.coupons = new ArrayBlockingQueue<>(number);
      this.commandService = new SecKillCommandService<>(coupons, number);
      this.persistentRunner = new SecKillPersistentRunner<>(coupons, discount, couponRepository);
      persistentRunner.run();
      couponRepository.save(currentEvent);
      //init completed
      available = true;
      return true;
    } else {
      return false;
    }
  }

  public SecKillCode seckill(String customerId){
    if(available) {
      SecKillCode result = commandService.addCouponTo(customerId);
      if (result == SecKillCode.Finish) {
        CouponEvent event = new CouponEvent(CouponEventType.Finish, currentEvent.getId(),
            commandService.getTotalCoupons(), discount);
        couponRepository.save(event);
        //close current seckill
        available = false;
      }
      return result;
    } else {
      return SecKillCode.Failed;
    }
  }

}
