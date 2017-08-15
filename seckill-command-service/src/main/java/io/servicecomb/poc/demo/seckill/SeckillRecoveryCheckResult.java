package io.servicecomb.poc.demo.seckill;

import java.util.HashSet;
import java.util.Set;

public class SeckillRecoveryCheckResult {
  private boolean startEventAvailable;
  private boolean finishEventAvailable;
  private int leftCount;
  private Set<String> claimedCustomers;

  public boolean isStartEventAvailable() {
    return startEventAvailable;
  }

  public int getLeftCount() {
    return leftCount;
  }

  public void setStartEventAvailable(boolean startEventAvailable) {
    this.startEventAvailable = startEventAvailable;
  }

  public void setLeftCount(int leftCount) {
    this.leftCount = leftCount;
  }

  public boolean isFinishEventAvailable() {
    return finishEventAvailable;
  }

  public void setFinishEventAvailable(boolean finishEventAvailable) {
    this.finishEventAvailable = finishEventAvailable;
  }

  public Set<String> getClaimedCustomers() {
    return claimedCustomers;
  }

  public SeckillRecoveryCheckResult(int leftCount) {
    startEventAvailable = false;
    finishEventAvailable = false;
    this.leftCount = leftCount;
    this.claimedCustomers = new HashSet<>();
  }
}
