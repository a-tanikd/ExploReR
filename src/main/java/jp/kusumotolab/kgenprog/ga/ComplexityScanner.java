package jp.kusumotolab.kgenprog.ga;

import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;

public class ComplexityScanner extends MetricScanner {

  private int complexity = 0;

  /**
   * @return The sum of Cyclomatic Complexity of each method in scanned class
   */
  @Override
  public double getMetric() {
    return complexity;
  }

  @Override
  public <T> void visitCtConstructor(CtConstructor<T> c) {
    if (!c.isImplicit()) {
      ++complexity;
    }
    super.visitCtConstructor(c);
  }

  @Override
  public <T> void visitCtMethod(CtMethod<T> m) {
    ++complexity;
    super.visitCtMethod(m);
  }

  @Override
  public void visitCtIf(final CtIf ifElement) {
    ++complexity;
    super.visitCtIf(ifElement);
  }

  @Override
  public void visitCtFor(final CtFor forLoop) {
    ++complexity;
    super.visitCtFor(forLoop);
  }

  @Override
  public void visitCtForEach(final CtForEach foreach) {
    ++complexity;
    super.visitCtForEach(foreach);
  }

  @Override
  public void visitCtWhile(final CtWhile whileLoop) {
    ++complexity;
    super.visitCtWhile(whileLoop);
  }

  @Override
  public void visitCtDo(final CtDo whileLoop) {
    ++complexity;
    super.visitCtDo(whileLoop);
  }

  @Override
  public <T> void visitCtConditional(final CtConditional<T> conditional) {
    ++complexity;
    super.visitCtConditional(conditional);
  }
}
