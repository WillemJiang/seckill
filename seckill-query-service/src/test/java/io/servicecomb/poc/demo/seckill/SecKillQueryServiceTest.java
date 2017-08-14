package io.servicecomb.poc.demo.seckill;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import io.servicecomb.poc.demo.CommandQueryApplication;
import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import io.servicecomb.poc.demo.seckill.event.CouponEventType;
import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import io.servicecomb.poc.demo.seckill.web.CouponInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommandQueryApplication.class)
@WebAppConfiguration
public class SecKillQueryServiceTest {

  private final int totalSecKillSuccessCount = 10;

  @Autowired
  private CouponEventRepository repository;

  @Autowired
  private SecKillEventSubscriber secKillEventSubscriber;

  @Test
  public void mutiQuerySuccess() throws Exception {

    //inject test data
    repository.deleteAll();
    List<CouponEvent> events = new ArrayList<>();
    //init seckill success data
    for (int i = 0; i < totalSecKillSuccessCount; i++) {
      events.add(new CouponEvent(CouponEventType.SecKill, String.valueOf(i), 1, (float) 0.7));
    }

    //add a finish seckill history
    CouponEvent start = new CouponEvent(CouponEventType.Start, totalSecKillSuccessCount, (float) 0.7);
    events.add(start);
    events.add(new CouponEvent(CouponEventType.Finish, start.getId(), totalSecKillSuccessCount, (float) 0.7));

    //add current seckill
    CouponEvent current = new CouponEvent(CouponEventType.Start, totalSecKillSuccessCount, (float) 0.8);
    events.add(current);

    repository.save(events);
    repository.flush();


    final AtomicInteger secKillSuccessSum = new AtomicInteger();
    final AtomicInteger customerIdGenerator = new AtomicInteger();
    int callCount = 100;

    CyclicBarrier barrier = new CyclicBarrier(callCount);

    addCouponsAsync(callCount
        , () -> {
          try {
            barrier.await();
            List<CouponInfo> infos = secKillEventSubscriber
                .querySuccessCoupon(String.valueOf(customerIdGenerator.getAndIncrement()));
            secKillSuccessSum.addAndGet(infos.size());
            return true;
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }, success -> {
        });

    Assert.assertThat(secKillSuccessSum.get(), is(totalSecKillSuccessCount));
  }

  @Test
  public void queryCurrent() {

    //inject test data
    repository.deleteAll();
    List<CouponEvent> events = new ArrayList<>();
    //add a finish seckill history
    CouponEvent start = new CouponEvent(CouponEventType.Start, totalSecKillSuccessCount, (float) 0.7);
    events.add(start);
    events.add(new CouponEvent(CouponEventType.Finish, start.getId(), totalSecKillSuccessCount, (float) 0.7));

    //add current seckill
    CouponEvent current = new CouponEvent(CouponEventType.Start, totalSecKillSuccessCount, (float) 0.8);
    events.add(current);

    repository.save(events);
    repository.flush();

    CouponInfo info = secKillEventSubscriber.queryCurrentCoupon();
    Assert.assertThat(info,is(notNullValue()));

    events.add(new CouponEvent(CouponEventType.Finish, current.getId(), totalSecKillSuccessCount, (float) 0.7));
    repository.save(events);
    repository.flush();

    info = secKillEventSubscriber.queryCurrentCoupon();
    Assert.assertThat(info,is(nullValue()));
  }

  private void addCouponsAsync(int threads, Supplier<Boolean> supplier, Consumer<Boolean> consumer) {
    ExecutorService executorService = Executors.newFixedThreadPool(threads);

    List<CompletableFuture<Void>> futures = new ArrayList<>();
    for (int i = 0; i < threads; i++) {
      futures.add(CompletableFuture.supplyAsync(supplier, executorService).thenAccept(consumer));
    }

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
    executorService.shutdown();
  }
}
