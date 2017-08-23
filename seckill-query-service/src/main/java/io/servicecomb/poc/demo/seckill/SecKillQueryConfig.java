package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecKillQueryConfig {

  @Bean
  SecKillEventSubscriber secKillEventSubscriber(SeckillEventLoader seckillEventLoader) {
    return new SecKillEventSubscriber(seckillEventLoader);
  }

  @Bean
  SeckillEventLoader seckillEventLoader(SpringBasedPromotionEventRepository promotionEventRepository,
      PromotionRepository promotionRepository) {
    SeckillEventLoader eventLoader = new SeckillEventLoader(promotionEventRepository,promotionRepository);
    eventLoader.run();
    return eventLoader;
  }
}
