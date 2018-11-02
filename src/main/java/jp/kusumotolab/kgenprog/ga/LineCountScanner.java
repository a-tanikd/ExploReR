package jp.kusumotolab.kgenprog.ga;

import spoon.reflect.declaration.CtClass;

public class LineCountScanner extends MetricScanner {

  private int lineCount;

  /**
   * @return The number of lines of scanned class
   */
  @Override
  public double getMetric() {
    return lineCount;
  }

  @Override
  public <T> void visitCtClass(CtClass<T> ctClass) {
    final int startLine = ctClass.getPosition().getLine();
    final int endLine = ctClass.getPosition().getEndLine();
    lineCount = endLine - startLine + 1;
  }
}
