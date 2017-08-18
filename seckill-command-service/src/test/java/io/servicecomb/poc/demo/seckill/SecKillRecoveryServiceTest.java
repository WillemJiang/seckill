/*
 *   Copyright 2017 Huawei Technologies Co., Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.servicecomb.poc.demo.seckill;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionFinishEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionGrabbedEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionStartEvent;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedCouponEventRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class SecKillRecoveryServiceTest {

  private final Promotion unpublishedPromotion = new Promotion(new Date(), 5, 0.7f);
  private final Promotion runningPromotion = new Promotion(new Date(), 5, 0.7f);
  private final Promotion endedPromotion = new Promotion(new Date(), 5, 0.7f);

  private SpringBasedCouponEventRepository repository = mock(SpringBasedCouponEventRepository.class);

  private SecKillRecoveryService recoveryService = new SecKillRecoveryService(repository);

  @Before
  public void setup() {
    when(repository.findByCouponId(unpublishedPromotion.getId()))
        .thenReturn(Collections.emptyList());

    List<PromotionEvent<String>> runningPromotionEvents = new ArrayList<>();
    runningPromotionEvents.add(new PromotionStartEvent<>(runningPromotion));
    runningPromotionEvents.add(new PromotionGrabbedEvent<>(runningPromotion, "zyy"));
    when(repository.findByCouponId(runningPromotion.getId()))
        .thenReturn(runningPromotionEvents);

    List<PromotionEvent<String>> endedPromotionEvents = new ArrayList<>();
    endedPromotionEvents.add(new PromotionStartEvent<>(endedPromotion));
    for (int i = 0; i < endedPromotion.getNumberOfCoupons(); i++) {
      endedPromotionEvents.add(new PromotionGrabbedEvent<>(endedPromotion, String.valueOf(i)));
    }
    endedPromotionEvents.add(new PromotionFinishEvent<>(endedPromotion));
    when(repository.findByCouponId(endedPromotion.getId()))
        .thenReturn(endedPromotionEvents);
  }

  @Test
  public void unstartPromotionCheck() {
    SeckillRecoveryCheckResult result = recoveryService.check(unpublishedPromotion);
    assertThat(result.isStarted(), is(false));
    assertThat(result.isFinished(), is(false));
    assertThat(result.remainingCoupons(), is(unpublishedPromotion.getNumberOfCoupons()));
    assertThat(result.getClaimedCustomers().isEmpty(), is(true));
  }

  @Test
  public void recoverPromotionCheck() {
    SeckillRecoveryCheckResult result = recoveryService.check(runningPromotion);
    assertThat(result.isStarted(), is(true));
    assertThat(result.isFinished(), is(false));
    assertThat(result.remainingCoupons(), is(runningPromotion.getNumberOfCoupons() - 1));
    assertThat(result.getClaimedCustomers(), contains("zyy"));
  }

  @Test
  public void finishPromotionCheck() {
    SeckillRecoveryCheckResult result = recoveryService.check(endedPromotion);
    assertThat(result.isStarted(), is(true));
    assertThat(result.isFinished(), is(true));
    assertThat(result.remainingCoupons(), is(0));
    assertThat(result.getClaimedCustomers(), contains("0","1", "2", "3", "4"));
  }
}
