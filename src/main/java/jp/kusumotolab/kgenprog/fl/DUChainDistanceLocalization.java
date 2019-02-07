package jp.kusumotolab.kgenprog.fl;

import org.eclipse.jdt.core.dom.CompilationUnit;
import jp.kusumotolab.kgenprog.analysis.DUChainDistanceVisitor;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedMethodName;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class DUChainDistanceLocalization extends SmellLocalization {

  public DUChainDistanceLocalization(
      final TargetFullyQualifiedMethodName refactoredMethodName) {
    super(refactoredMethodName);
  }

  @Override
  protected double calculateSmell(JDTASTLocation location) {
    final CompilationUnit compilationUnit = (CompilationUnit) location.node.getRoot();
    final DUChainDistanceVisitor visitor = new DUChainDistanceVisitor(compilationUnit);
    location.node.accept(visitor);

    return visitor.getDistance();
  }
}
