package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;

public class MetricFitness implements Fitness {

  private static double INITIAL_METRIC = -1;

  private final double metric;
  private final double testSuccessRate;

  public MetricFitness(final double metric, double testSuccessRate) {
    this.metric = metric;
    this.testSuccessRate = testSuccessRate;
  }

  public static void init(GeneratedSourceCode sourceCode,
      SourceCodeValidation validation) {

    if (INITIAL_METRIC != -1) {
      throw new UnsupportedOperationException();
    }

    Fitness initialFitness = validation.exec(sourceCode, EmptyTestResults.instance);
    INITIAL_METRIC = initialFitness.getValue();
  }

  @Override
  public double getValue() {
    return metric;
  }

  @Override
  public boolean isMaximum() {
    return testSuccessRate == 1.0 && metric < INITIAL_METRIC;
  }
}
