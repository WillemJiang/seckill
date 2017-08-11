//package io.servicecomb.poc.demo.seckill;
//
//import static java.util.concurrent.TimeUnit.SECONDS;
//import static org.awaitility.Awaitility.waitAtMost;
//import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
//import static org.junit.Assert.assertThat;
//
//import io.servicecomb.poc.demo.seckill.CouponEventRepository;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//import org.junit.Test;
//
//
//public class SecKillPersistentRunnerTest {
//  private final int numberOfCoupons = 5;
//
//  private final List<Integer> customerIds = new LinkedList<>();
//  private final CouponEventRepository repository = coupon -> customerIds.add(coupon.getCustomer_id());
//
//  private final BlockingQueue<Coupon<Integer>> coupons = new ArrayBlockingQueue<>(numberOfCoupons);
//
//  private final SecKillPersistentRunner<Integer> runner = new SecKillPersistentRunner<>(coupons, repository);
//
//  @Test
//  public void persistsCouponUsingRepo() {
//
//    double discount = 0.7;
//
//    for (int i = 0; i < numberOfCoupons; i++) {
//      coupons.offer(new Coupon<>(i,discount));
//    }
//
//    runner.run();
//
//    waitAtMost(2, SECONDS).until(coupons::isEmpty);
//    assertThat(customerIds, contains(0, 1, 2, 3, 4));
//  }
//}
