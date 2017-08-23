package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SecKillEventSubscriber {

  @Autowired
  private SpringBasedPromotionEventRepository promotionEventRepository;

  @Autowired
  private PromotionRepository promotionRepository;

  public SecKillEventSubscriber(SpringBasedPromotionEventRepository promotionEventRepository,
                                PromotionRepository promotionRepository) {
    this.promotionEventRepository = promotionEventRepository;
    this.promotionRepository = promotionRepository;
  }

  public List<Coupon> querySuccessCoupon(String customerId){
    return promotionEventRepository.findByCustomerId(customerId).stream().map(
            event -> new Coupon(event.getId(),
                    event.getPromotionId(),
                    event.getTime(),
                    event.getDiscount(),
                    event.getCustomerId())
    ).collect(Collectors.toList());
  }

  public List<Promotion> queryCurrentPromotion(){
    List<PromotionEvent> startEvents = promotionEventRepository.findByTypeOrderByTimeDesc(PromotionEventType.Start);

    List<Promotion> activepromotions = new ArrayList<Promotion>();

    for (PromotionEvent startEvent : startEvents) {
      String promotionId = startEvent.getPromotionId();
      PromotionEvent finishEvent = promotionEventRepository.findTopByPromotionIdAndTypeOrderByIdDesc(promotionId,PromotionEventType.Finish);
      if(finishEvent == null){
        Promotion promotionElem = promotionRepository.findByPromotionId(promotionId);
        activepromotions.add(promotionElem);
      }
    }

    return activepromotions;
  }
}
