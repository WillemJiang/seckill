package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SeckillEventLoader<T> {
  private SpringBasedPromotionEventRepository promotionEventRepository;
  private PromotionRepository promotionRepository;

  public SeckillEventLoader(SpringBasedPromotionEventRepository promotionEventRepository,
      PromotionRepository promotionRepository){
    this.promotionEventRepository = promotionEventRepository;
    this.promotionRepository = promotionRepository;
  }

  public void run() {
    CompletableFuture.runAsync(() -> {
      int currentPromotionIndex = 0;
      int currentPromotionEventIndex = 0;

      while (!Thread.currentThread().isInterrupted()) {
        try {
          currentPromotionEventIndex = saveSuccessCoupons(currentPromotionEventIndex);
          currentPromotionIndex = saveCurrentPromotions(currentPromotionIndex);
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          System.out.println();
          Thread.currentThread().interrupt();
        }
      }
    });
  }

  private final Map<T,List<Coupon>> customerCoupons = new HashMap<>();
  private final Map<String,List<Promotion>> activePromotions = new HashMap<>();

  public List<Coupon> getCustomerCoupons(T customerId){
    return customerCoupons.get(customerId);
  }

  public List<Promotion> getActivePromotions(){
    return activePromotions.get("CurrentPromotions");
  }

  public int saveSuccessCoupons(int promotionEventIndex){
    List<PromotionEvent> promotionEvents = promotionEventRepository.findByIdGreaterThan(promotionEventIndex);
    for (PromotionEvent promotionEvent : promotionEvents) {
      T customerId = (T)promotionEvent.getCustomerId();

      if(!customerCoupons.containsKey(customerId)) {
        customerCoupons.put((T)customerId, new ArrayList<>());
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

  public int saveCurrentPromotions(int promotionIndex) {
    List<Promotion> promotions = promotionRepository.findByIdGreaterThan(promotionIndex);

    for (Promotion promotion : promotions) {
      String currentPromotionId = promotion.getPromotionId();

      PromotionEvent startEvent = promotionEventRepository.findTopByPromotionIdAndTypeOrderByIdDesc(
          currentPromotionId, PromotionEventType.Start);
      if (startEvent != null) {
        PromotionEvent finishEvent = promotionEventRepository.findTopByPromotionIdAndTypeOrderByIdDesc(
            currentPromotionId, PromotionEventType.Finish);
        if(finishEvent == null){
          if(!activePromotions.containsKey("CurrentPromotions")){
            activePromotions.put("CurrentPromotions", new ArrayList<>());
          }

          activePromotions.get("CurrentPromotions").add(promotion);
        }
      }

      promotionIndex = promotion.getId();
    }

    return promotionIndex;
  }
}
