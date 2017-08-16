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
import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedCouponEventRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;

public class SecKillRecoveryServiceTest {

  private final Promotion unstartPromotion = new Promotion(new Date(), 5, 0.7f);
  private final Promotion needRecoverPromotion = new Promotion(new Date(), 5, 0.7f);
  private final Promotion finishPromotion = new Promotion(new Date(), 5, 0.7f);

  private SpringBasedCouponEventRepository repository = null;

  private SecKillRecoveryService recoveryService = null;

  public SecKillRecoveryServiceTest() {
    repository = mock(SpringBasedCouponEventRepository.class);

    when(repository.findTopByCouponIdAndTypeOrderByIdDesc(unstartPromotion.getId(), PromotionEventType.Start))
        .thenReturn(null);
    when(repository.findTopByCouponIdAndTypeOrderByIdDesc(needRecoverPromotion.getId(), PromotionEventType.Start))
        .thenReturn(PromotionEvent.genStartCouponEvent(needRecoverPromotion));
    when(repository.findTopByCouponIdAndTypeOrderByIdDesc(finishPromotion.getId(), PromotionEventType.Start))
        .thenReturn(PromotionEvent.genStartCouponEvent(finishPromotion));

    when(repository.findByCouponIdAndIdGreaterThan(unstartPromotion.getId(), 0))
        .thenReturn(new ArrayList<>());

    List<PromotionEvent<String>> needRecoverPromotionEvents = new ArrayList<>();
    needRecoverPromotionEvents.add(PromotionEvent.genSecKillCouponEvent(needRecoverPromotion, "zyy"));
    when(repository.findByCouponIdAndIdGreaterThan(needRecoverPromotion.getId(), 0))
        .thenReturn(needRecoverPromotionEvents);

    List<PromotionEvent<String>> finishPromotionEvents = new ArrayList<>();
    for (int i = 0; i < finishPromotion.getNumberOfCoupons(); i++) {
      finishPromotionEvents.add(PromotionEvent.genSecKillCouponEvent(finishPromotion, String.valueOf(i)));
    }
    finishPromotionEvents.add(PromotionEvent.genFinishCouponEvent(finishPromotion));
    when(repository.findByCouponIdAndIdGreaterThan(finishPromotion.getId(), 0))
        .thenReturn(finishPromotionEvents);

    recoveryService = new SecKillRecoveryService(repository);
  }

  @Test
  public void unstartPromotionCheck() {
    SeckillRecoveryCheckResult result = recoveryService.check(unstartPromotion);
    assertThat(result.isStartEventAvailable(), is(false));
    assertThat(result.isFinishEventAvailable(), is(false));
    assertThat(result.remainingCoupons(), is(unstartPromotion.getNumberOfCoupons()));
    assertThat(result.getClaimedCustomers().isEmpty(), is(true));
  }

  @Test
  public void recoverPromotionCheck() {
    SeckillRecoveryCheckResult result = recoveryService.check(needRecoverPromotion);
    assertThat(result.isStartEventAvailable(), is(true));
    assertThat(result.isFinishEventAvailable(), is(false));
    assertThat(result.remainingCoupons(), is(needRecoverPromotion.getNumberOfCoupons() - 1));
    assertThat(result.getClaimedCustomers(), contains("zyy"));
  }

  @Test
  public void finishPromotionCheck() {
    SeckillRecoveryCheckResult result = recoveryService.check(finishPromotion);
    assertThat(result.isStartEventAvailable(), is(true));
    assertThat(result.isFinishEventAvailable(), is(true));
    assertThat(result.remainingCoupons(), is(0));
    assertThat(result.getClaimedCustomers(), contains("0","1", "2", "3", "4"));
  }
}
