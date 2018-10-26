package jp.kusumotolab.kgenprog.fl;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.ASTLocation;

public class Suspiciousness {

  private static Logger log = LoggerFactory.getLogger(Suspiciousness.class);

  // 疑惑値とその場所のリスト
  final private ASTLocation location;
  final private double value;

  public Suspiciousness(ASTLocation location, double value) {
    this.location = location;
    this.value = value;
  }

  public ASTLocation getLocation() {
    log.debug("enter getLocation()");
    return location;
  }

  public double getValue() {
    log.debug("enter getValue()");
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Suspiciousness that = (Suspiciousness) o;
    return Double.compare(that.getValue(), getValue()) == 0 &&
        Objects.equals(getLocation(), that.getLocation());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getLocation(), getValue());
  }
}
