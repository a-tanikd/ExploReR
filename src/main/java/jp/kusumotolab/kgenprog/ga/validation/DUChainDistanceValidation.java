package jp.kusumotolab.kgenprog.ga.validation;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.analysis.DUChainDistanceVisitor;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

public class DUChainDistanceValidation extends MetricValidation {

  private Logger log = LoggerFactory.getLogger(DUChainDistanceValidation.class);

  /**
   * Calculate the sum of DU-Chain Distance of each method in the specified GeneratedSourceCode
   *
   * @param sourceCode GeneratedSourceCode to be calculated fitness of
   * @return the sum of Cyclomatic Complexity of the specified GeneratedSourceCode
   */
  @Override
  protected double calculateFitness(GeneratedSourceCode sourceCode) {
    double totalDistance = 0;

    // calculate sum of DU-Chain Distance of each method in each class
    for (GeneratedAST ast : sourceCode.getProductAsts()) {
      final CompilationUnit compilationUnit = ((GeneratedJDTAST<ProductSourcePath>) ast).getRoot();
      final DUChainDistanceVisitor visitor = new DUChainDistanceVisitor(compilationUnit);

      log.debug("\n{}", ast.getSourceCode());

      compilationUnit.accept(visitor);
      totalDistance += visitor.getDistance();
    }

    return totalDistance;
  }
}
