package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SeckillEventLoader<T> {
  private SpringBasedPromotionEventRepository promotionEventRepository;
  private PromotionRepository promotionRepository;

  public SeckillEventLoader(SpringBasedPromotionEventRepository promotionEventRepository,
      PromotionRepository promotionRepository){
    this.promotionEventRepository = promotionEventRepository;
    this.promotionRepository = promotionRepository;
  }

  public void reloadEventsScheduler() {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleWithFixedDelay(new Runnable() {
                                      int currentPromotionEventIndex = 0;
                                      int currentPromotionIndex = 0;

                                      @Override
                                      public void run() {
                                        currentPromotionIndex = reloadActivePromotions(currentPromotionEventIndex, currentPromotionIndex);
                                        currentPromotionEventIndex = reloadSuccessCoupons(currentPromotionEventIndex);
                                      }
                                    },
        0,
        1000,
        TimeUnit.MILLISECONDS);
  }

  private final Map<T,List<Coupon>> customerCoupons = new HashMap<>();
  private final List<Promotion> activePromotions = new ArrayList<>();

  public List<Coupon> getCustomerCoupons(T customerId){
    return customerCoupons.get(customerId);
  }

  public List<Promotion> getActivePromotions(){
    return activePromotions;
  }

  public int reloadSuccessCoupons(int promotionEventIndex){

    List<PromotionEvent> promotionEvents = promotionEventRepository.findByIdGreaterThan(promotionEventIndex);

    for (PromotionEvent promotionEvent : promotionEvents) {
      T customerId = (T) promotionEvent.getCustomerId();

      if(!customerCoupons.containsKey(customerId)) {
        customerCoupons.put(customerId, new ArrayList<>());
      }

      customerCoupons.get(customerId).add(
          new Coupon(promotionEvent.getId(),
              promotionEvent.getPromotionId(),
              promotionEvent.getTime(),
              promotionEvent.getDiscount(),
              promotionEvent.getCustomerId())
      );

      promotionEventIndex = promotionEvent.getId();
    }

    return promotionEventIndex;
  }

  public int reloadActivePromotions (int promotionEventIndex,int promotionIndex) {
    List<PromotionEvent> promotionEvents = promotionEventRepository.findByIdGreaterThan(promotionEventIndex);

    for (PromotionEvent promotionEvent : promotionEvents) {
      String currentPromotionId = promotionEvent.getPromotionId();

      PromotionEvent startEvent = promotionEventRepository.findTopByPromotionIdAndTypeOrderByIdDesc(
          currentPromotionId, PromotionEventType.Start);
      if (startEvent != null) {
        PromotionEvent finishEvent = promotionEventRepository.findTopByPromotionIdAndTypeOrderByIdDesc(
            currentPromotionId, PromotionEventType.Finish);
        if(finishEvent == null){
          Promotion activepromotion = promotionRepository.findTopByPromotionId(currentPromotionId);
          activePromotions.add(activepromotion);
        }
      }

      promotionEventIndex = promotionEvent.getId();
    }

    return promotionIndex;
  }
}
