package jp.kusumotolab.kgenprog.fl;

import jp.kusumotolab.kgenprog.analysis.ComplexityVisitor;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class ComplexityLocalization extends SmellLocalization {

  @Override
  protected double calculateSmell(JDTASTLocation location) {
    final ComplexityVisitor visitor = new ComplexityVisitor();
    location.node.accept(visitor);

    return visitor.getComplexity();
  }
}
