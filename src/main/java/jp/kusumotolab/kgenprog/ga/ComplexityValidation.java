package jp.kusumotolab.kgenprog.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class ComplexityValidation extends MetricValidation {

  private Logger log = LoggerFactory.getLogger(ComplexityScanner.class);

  /**
   * Calculate the average of Cyclomatic Complexity of each method in the specified
   * GeneratedSourceCode
   *
   * @param sourceCode GeneratedSourceCode to be calculated fitness of
   * @return the sum of Cyclomatic Complexity of the specified GeneratedSourceCode
   */
  @Override
  protected double calculateFitness(GeneratedSourceCode sourceCode) {
    final ComplexityScanner scanner = new ComplexityScanner();

    // calculate sum of Cyclomatic Complexity of each method in each class
    for (GeneratedAST ast : sourceCode.getProductAsts()) {
      log.debug("\n{}", ast.getSourceCode());
      final CtClass clazz = Launcher.parseClass(ast.getSourceCode());
      clazz.accept(scanner);
    }

    return scanner.getMetric() / sourceCode.getProductAsts()
        .size();
  }
}
