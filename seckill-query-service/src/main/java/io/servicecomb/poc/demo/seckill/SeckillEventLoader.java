package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SeckillEventLoader<T> {
  private SpringBasedPromotionEventRepository promotionEventRepository;
  private PromotionRepository promotionRepository;

  public SeckillEventLoader(SpringBasedPromotionEventRepository promotionEventRepository,
                            PromotionRepository promotionRepository){
    this.promotionEventRepository = promotionEventRepository;
    this.promotionRepository = promotionRepository;
  }

  public void reloadEvents() {
    CompletableFuture.runAsync(() -> {
      int currentPromotionEventIndex = 0;
      int currentPromotionIndex = 0;

      while (!Thread.currentThread().isInterrupted()) {
        try {
          currentPromotionIndex = reloadActivePromotions(currentPromotionEventIndex, currentPromotionIndex);
          currentPromotionEventIndex = reloadSuccessCoupons(currentPromotionEventIndex);
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
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
