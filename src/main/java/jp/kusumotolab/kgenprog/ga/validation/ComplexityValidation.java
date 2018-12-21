package jp.kusumotolab.kgenprog.ga.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.analysis.ComplexityVisitor;
import jp.kusumotolab.kgenprog.ga.ComplexityScanner;
import jp.kusumotolab.kgenprog.ga.validation.MetricValidation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

public class ComplexityValidation extends MetricValidation {

  private Logger log = LoggerFactory.getLogger(ComplexityScanner.class);

  /**
   * Calculate the sum of Cyclomatic Complexity of each method in the specified
   * GeneratedSourceCode
   *
   * @param sourceCode GeneratedSourceCode to be calculated fitness of
   * @return the sum of Cyclomatic Complexity of the specified GeneratedSourceCode
   */
  @Override
  protected double calculateFitness(GeneratedSourceCode sourceCode) {
    final ComplexityVisitor visitor = new ComplexityVisitor();

    // calculate sum of Cyclomatic Complexity of each method in each class
    for (GeneratedAST ast : sourceCode.getProductAsts()) {
      log.debug("\n{}", ast.getSourceCode());

      // todo: remove down cast
      ((GeneratedJDTAST<ProductSourcePath>)ast).getRoot().accept(visitor);
    }

    return visitor.getComplexity();
  }
}
