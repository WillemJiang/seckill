package io.servicecomb.poc.demo.seckill;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

import io.servicecomb.poc.demo.CommandServiceApplication;
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
    classes = CommandServiceApplication.class,
    webEnvironment = RANDOM_PORT)
public class SecKillCommandServiceApplicationTest {

  private final AtomicInteger numberOfSuccess = new AtomicInteger();
  private final AtomicInteger customerIdGenerator = new AtomicInteger();

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void createSecKill() throws Exception {
    int number = 3;
    HttpHeaders headers = headers();
    ResponseEntity<Boolean> createEntity = restTemplate.exchange(
        "/command/start/{number}/{discount}",
        GET,
        new HttpEntity<String>(headers),
        Boolean.class,
        number,
        0.7);

    assertThat(createEntity.getStatusCode(), is(OK));
    assertThat(createEntity.getBody().toString().toLowerCase(), is("true"));
  }

  @Test
  public void overSecKill() throws Exception {

    int total = 10;
    final HttpHeaders headers = headers();
    ResponseEntity<Boolean> createEntity = restTemplate.exchange(
        "/command/start/{number}/{discount}",
        GET,
        new HttpEntity<String>(headers),
        Boolean.class,
        total,
        0.7);

    assertThat(createEntity.getStatusCode(), is(OK));

    int threads = 50;
    CyclicBarrier barrier = new CyclicBarrier(threads);

    addCouponsAsync(threads
        , () -> {
          try {
            barrier.await();
            ResponseEntity<Boolean> seckillEntity = restTemplate.exchange(
                "/command/seckill/{id}",
                GET,
                new HttpEntity<String>(headers),
                Boolean.class,
                customerIdGenerator.getAndIncrement());
            return seckillEntity.getStatusCode().is2xxSuccessful() && seckillEntity.getBody().toString().toLowerCase()
                .equals("true");
          } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
          }
        }, success -> {
          if (success) {
            numberOfSuccess.incrementAndGet();
          }
        });

    Assert.assertThat(numberOfSuccess.get(), is(total));
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
