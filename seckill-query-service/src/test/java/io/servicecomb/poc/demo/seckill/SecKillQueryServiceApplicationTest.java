package io.servicecomb.poc.demo.seckill;

import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.GET;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.poc.demo.CommandQueryApplication;
import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import io.servicecomb.poc.demo.seckill.event.CouponEventType;
import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import io.servicecomb.poc.demo.seckill.web.CouponInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = CommandQueryApplication.class,
    webEnvironment = RANDOM_PORT)
public class SecKillQueryServiceApplicationTest {

  private final int totalSecKillSuccessCount = 10;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private CouponEventRepository repository;

  //inject test data
  @Before
  public void tearUp() {
    repository.deleteAll();
    List<CouponEvent> events = new ArrayList<>();
    //init seckill success data
    for(int i = 0;i < totalSecKillSuccessCount;i++) {
      events.add(new CouponEvent(CouponEventType.SecKill,String.valueOf(i),1,(float)0.7));
    }

    //add a finish seckill history
    CouponEvent start = new CouponEvent(CouponEventType.Start,totalSecKillSuccessCount,(float)0.7);
    events.add(start);
    events.add(new CouponEvent(CouponEventType.Finish,start.getId(), totalSecKillSuccessCount,(float)0.7));

    //add current seckill
    CouponEvent current = new CouponEvent(CouponEventType.Start,totalSecKillSuccessCount,(float)0.8);
    events.add(current);

    repository.save(events);
    repository.flush();
  }


  @Test
  public void mutiQuerySuccess() throws Exception {

    final AtomicInteger callSuccessCount = new AtomicInteger();
    final AtomicInteger secKillSuccessSum = new AtomicInteger();
    final AtomicInteger customerIdGenerator = new AtomicInteger();
    int callCount = 100;

    final HttpHeaders headers = headers();
    CyclicBarrier barrier = new CyclicBarrier(callCount);

    addCouponsAsync(callCount
        , () -> {
          try {
            barrier.await();
            ResponseEntity<String> successEntity = restTemplate.exchange(
                "/query/success/{id}",
                GET,
                new HttpEntity<String>(headers),
                String.class,
                customerIdGenerator.getAndIncrement());

            if(successEntity.getStatusCode().is2xxSuccessful()) {

              System.out.println("query result : "+ successEntity.getBody());

              ObjectMapper mapper = new ObjectMapper();
              CouponInfo[] infos = mapper.readValue(successEntity.getBody(),CouponInfo[].class);
              secKillSuccessSum.addAndGet(infos.length);
              return true;
            } else {
              return false;
            }
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }, success -> {
          if (success) {
            callSuccessCount.incrementAndGet();
          }
        });
    Assert.assertThat(callSuccessCount.get(), is(callCount));
    Assert.assertThat(secKillSuccessSum.get(), is(totalSecKillSuccessCount));
  }

  @Test
  public void mutiQueryCurrent() throws Exception {

    final AtomicInteger callSuccessCount = new AtomicInteger();
    int callCount = 100;

    final HttpHeaders headers = headers();

    CyclicBarrier barrier = new CyclicBarrier(callCount);
    addCouponsAsync(callCount
        , () -> {
          try {
            barrier.await();
            ResponseEntity<String> currentEntity = restTemplate.exchange(
                "/query/current",
                GET,
                new HttpEntity<String>(headers),
                String.class);

            return currentEntity.getStatusCode().is2xxSuccessful();
          } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
          }
        }, success -> {
          if (success) {
            callSuccessCount.incrementAndGet();
          }
        });

    Assert.assertThat(callSuccessCount.get(), is(callCount));
  }


  private HttpHeaders headers() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(singletonList(MediaType.APPLICATION_JSON));
    return headers;
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
