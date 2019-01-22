package jp.kusumotolab.kgenprog.fl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedMethodName;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public abstract class SmellLocalization implements FaultLocalization {

  protected final TargetFullyQualifiedMethodName refactoredMethodName;

  public SmellLocalization(final TargetFullyQualifiedMethodName refactoredMethodName) {
    this.refactoredMethodName = refactoredMethodName;
  }

  @Override
  public List<Suspiciousness> exec(GeneratedSourceCode generatedSourceCode,
      TestResults testResults) {
    final List<Suspiciousness> suspiciousnesses = new ArrayList<>();

    for (final GeneratedAST ast : generatedSourceCode.getProductAsts()) {
      if (!ast.getPrimaryClassName()
          .equals(refactoredMethodName.getClassName())) {
        continue;
      }

      final int lastLineNumber = ast.getNumberOfLines();
      final ASTLocations astLocations = ast.createLocations();

      for (int line = 1; line <= lastLineNumber; ++line) {
        final List<ASTLocation> locations = astLocations.infer(line);

        if (locations.isEmpty()) {
          continue;
        }

        final List<ASTLocation> locationsInSpecifiedMethod = locations.stream()
            .filter(location -> getEnclosingMethodName(((JDTASTLocation) location).node).equals(
                refactoredMethodName.getMethodName()))
            .collect(Collectors.toList());
        final List<ASTLocation> filteredLocations = filterLocations(locationsInSpecifiedMethod);

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

  private String getEnclosingMethodName(final ASTNode node) {
    ASTNode current = node.getParent();

    while (!isMethod(current)) {
      current = current.getParent();
    }

    return ((MethodDeclaration) current).getName()
        .toString();
  }

  private boolean isMethod(ASTNode node) {
    return node.getNodeType() == ASTNode.METHOD_DECLARATION;
  }

  protected List<ASTLocation> filterLocations(final List<ASTLocation> locations) {
    // By default, do nothing
    return locations;
  }

  abstract protected double calculateSmell(JDTASTLocation location);
}
