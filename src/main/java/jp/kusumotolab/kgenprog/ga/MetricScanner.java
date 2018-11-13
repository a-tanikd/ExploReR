package jp.kusumotolab.kgenprog.ga;

import spoon.reflect.visitor.CtScanner;

public abstract class MetricScanner extends CtScanner {

  public abstract double getMetric();
}
