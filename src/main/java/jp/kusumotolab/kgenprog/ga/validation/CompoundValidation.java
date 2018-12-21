package jp.kusumotolab.kgenprog.ga.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.ga.ComplexityScanner;
import jp.kusumotolab.kgenprog.ga.LineCountScanner;
import jp.kusumotolab.kgenprog.ga.NestingScanner;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class CompoundValidation extends MetricValidation {

  private static final Logger log = LoggerFactory.getLogger(CompoundValidation.class);

  @Override
  protected double calculateFitness(GeneratedSourceCode sourceCode) {
    double totalMetric = 0;

    for (GeneratedAST ast : sourceCode.getProductAsts()) {
      log.debug("\n{}", ast.getSourceCode());

      final CtClass clazz = Launcher.parseClass(ast.getSourceCode());
      final ComplexityScanner complexityScanner = new ComplexityScanner();
      final LineCountScanner lineCountScanner = new LineCountScanner();
      final NestingScanner nestingScanner = new NestingScanner();

      complexityScanner.scan(clazz);
      lineCountScanner.scan(clazz);
      nestingScanner.scan(clazz);

      totalMetric +=
          complexityScanner.getMetric() + lineCountScanner.getMetric() + nestingScanner.getMetric();
    }

    return totalMetric / sourceCode.getProductAsts()
        .size();
  }
}
