package jp.kusumotolab.kgenprog.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class MetricValidation implements SourceCodeValidation {

  private static final Logger log = LoggerFactory.getLogger(MetricValidation.class);

  @Override
  public Fitness exec(GeneratedSourceCode sourceCode, TestResults testResults) {
    if (!sourceCode.isGenerationSuccess()) {
      return new SimpleFitness(testResults.getSuccessRate());
    }
    final double fitness = calculateFitness(sourceCode);

    return new MetricFitness(fitness, testResults.getSuccessRate());
  }

  private double calculateFitness(GeneratedSourceCode sourceCode) {
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
