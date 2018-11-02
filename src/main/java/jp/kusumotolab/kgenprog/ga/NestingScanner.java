package jp.kusumotolab.kgenprog.ga;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;

public class NestingScanner extends MetricScanner {

  private int totalMaxNesting = 0;
  private int currentNesting = 0;
  private int currentMaxNesting = 0;

  /**
   * @return The sum of Max Nesting of each method
   */
  @Override
  public double getMetric() {
    return totalMaxNesting;
  }

  @Override
  public <T> void visitCtConstructor(CtConstructor<T> c) {
    if (c.isImplicit()) {
      return;
    }

    currentNesting = 0;
    currentMaxNesting = 0;
    super.visitCtConstructor(c);
    totalMaxNesting += currentMaxNesting;
  }

  @Override
  public <T> void visitCtMethod(CtMethod<T> m) {
    currentNesting = 0;
    currentMaxNesting = 0;
    super.visitCtMethod(m);
    totalMaxNesting += currentMaxNesting;
  }

  @Override
  public void visitCtIf(final CtIf ifElement) {
    ++currentNesting;
    currentMaxNesting = Math.max(currentMaxNesting, currentNesting);
    super.visitCtIf(ifElement);
    --currentNesting;
  }

  @Override
  public void visitCtFor(final CtFor forLoop) {
    ++currentNesting;
    currentMaxNesting = Math.max(currentMaxNesting, currentNesting);
    super.visitCtFor(forLoop);
    --currentNesting;
  }

  @Override
  public void visitCtForEach(final CtForEach foreach) {
    ++currentNesting;
    currentMaxNesting = Math.max(currentMaxNesting, currentNesting);
    super.visitCtForEach(foreach);
    --currentNesting;
  }

  @Override
  public void visitCtWhile(final CtWhile whileLoop) {
    ++currentNesting;
    currentMaxNesting = Math.max(currentMaxNesting, currentNesting);
    super.visitCtWhile(whileLoop);
    --currentNesting;
  }

  @Override
  public void visitCtDo(final CtDo whileLoop) {
    ++currentNesting;
    currentMaxNesting = Math.max(currentMaxNesting, currentNesting);
    super.visitCtDo(whileLoop);
    --currentNesting;
  }

  @Override
  public void visitCtTry(CtTry tryBlock) {
    ++currentNesting;
    currentMaxNesting = Math.max(currentMaxNesting, currentNesting);
    super.visitCtTry(tryBlock);
    --currentNesting;
  }

  @Override
  public void visitCtCatch(CtCatch catchBlock) {
    ++currentNesting;
    currentMaxNesting = Math.max(currentMaxNesting, currentNesting);
    super.visitCtCatch(catchBlock);
    --currentNesting;
  }

  @Override
  public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
    ++currentNesting;
    currentMaxNesting = Math.max(currentMaxNesting, currentNesting);
    super.visitCtTryWithResource(tryWithResource);
    --currentNesting;
  }
}
