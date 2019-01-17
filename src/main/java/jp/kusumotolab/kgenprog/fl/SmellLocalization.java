package jp.kusumotolab.kgenprog.fl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public abstract class SmellLocalization implements FaultLocalization {

  protected final TargetFullyQualifiedName refactoredClass;

  public SmellLocalization(final TargetFullyQualifiedName refactoredClass) {
    this.refactoredClass = refactoredClass;
  }

  @Override
  public List<Suspiciousness> exec(GeneratedSourceCode generatedSourceCode,
      TestResults testResults) {
    final List<Suspiciousness> suspiciousnesses = new ArrayList<>();

    for (final GeneratedAST ast : generatedSourceCode.getProductAsts()) {
      if (!ast.getPrimaryClassName()
          .equals(refactoredClass)) {
        continue;
      }

      final int lastLineNumber = ast.getNumberOfLines();
      final ASTLocations astLocations = ast.createLocations();

      for (int line = 1; line <= lastLineNumber; ++line) {
        final List<ASTLocation> locations = astLocations.infer(line);

        if (locations.isEmpty()) {
          continue;
        }

        final List<ASTLocation> filteredLocations = filterLocations(locations);

        for (ASTLocation location : filteredLocations) {
          final JDTASTLocation jdtastLocation = (JDTASTLocation) location;
          final double smell = calculateSmell(jdtastLocation);
          suspiciousnesses.add(new Suspiciousness(location, smell));
        }
      }
    }

    return suspiciousnesses.stream()
        .distinct()
        .collect(Collectors.toList());
  }

  protected List<ASTLocation> filterLocations(final List<ASTLocation> locations) {
    // By default, do nothing
    return locations;
  }

  abstract protected double calculateSmell(JDTASTLocation location);
}
