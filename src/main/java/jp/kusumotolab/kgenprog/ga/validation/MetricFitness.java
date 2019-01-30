package jp.kusumotolab.kgenprog.ga.validation;

public class MetricFitness implements Fitness {

  private static double INITIAL_METRIC = -1;

  private final double metric;
  private final double testSuccessRate;

  public MetricFitness(final double metric, double testSuccessRate) {
    this.metric = metric;
    this.testSuccessRate = testSuccessRate;
  }

  public static void init(double fitness) {

    if (INITIAL_METRIC != -1) {
      throw new UnsupportedOperationException();
    }

    INITIAL_METRIC = fitness;
  }

  public boolean isImproved() {
    return testSuccessRate == 1.0 && metric < INITIAL_METRIC;
  }

  @Override
  public double getValue() {
    return metric;
  }

  @Override
  public boolean isMaximum() {
    return false;
  }

  @Override
  public int compareTo(Fitness anotherFitness) {
    return Double.compare(getValue(), anotherFitness.getValue());
  }
}
