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

import io.servicecomb.poc.demo.seckill.event.CouponGrabbedEvent;
import io.servicecomb.poc.demo.seckill.repositories.spring.SpringCouponRepository;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SecKillCouponPersistentRunner<T> {

  private final BlockingQueue<CouponGrabbedEvent<T>> events;
  private final SpringCouponRepository<T> repository;

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

  public SecKillCouponPersistentRunner(BlockingQueue<CouponGrabbedEvent<T>> events,
      SpringCouponRepository<T> repository) {
    this.events = events;
    this.repository = repository;
  }

  public void run() {
    final Runnable executor = () -> {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          CouponGrabbedEvent<T> event = events.take();
          repository.save(event.getCoupon());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    };

    executorService.execute(executor);
  }
}
