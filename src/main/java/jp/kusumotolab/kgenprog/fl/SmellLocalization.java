package jp.kusumotolab.kgenprog.fl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class SmellLocalization implements FaultLocalization {

  @Override
  public List<Suspiciousness> exec(GeneratedSourceCode generatedSourceCode,
      TestResults testResults) {
    final List<Suspiciousness> suspiciousnesses = new ArrayList<>();

    for (final GeneratedAST ast : generatedSourceCode.getProductAsts()) {
      final String code = ast.getSourceCode();
      final int lastLineNumber = countLines(code);

      for (int line = 1; line <= lastLineNumber; ++line) {
        // todo: remove down cast
        final List<JDTASTLocation> locations = ast.inferLocations(line);

        if (locations.isEmpty()) {
          continue;
        }

        for (JDTASTLocation location : locations) {
          final ComplexityVisitor visitor = new ComplexityVisitor();
          location.node.accept(visitor);
          suspiciousnesses.add(new Suspiciousness(location, visitor.getComplexity()));
        }
      }
    }

    return suspiciousnesses.stream()
        .distinct()
        .collect(Collectors.toList());
  }

  private int countLines(final String text) {
    final String[] lines = text.split("\r\n|\r|\n");
    return lines.length;
  }

  static private class ComplexityVisitor extends ASTVisitor {

    private int complexity = 0;

    public int getComplexity() {
      return complexity;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
      ++complexity;
      return super.visit(node);
    }

    @Override
    public boolean visit(ConditionalExpression node) {
      ++complexity;
      return super.visit(node);
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
      ++complexity;
      return super.visit(node);
    }

    @Override
    public boolean visit(ForStatement node) {
      ++complexity;
      return super.visit(node);
    }

    @Override
    public boolean visit(DoStatement node) {
      ++complexity;
      return super.visit(node);
    }

    @Override
    public boolean visit(WhileStatement node) {
      ++complexity;
      return super.visit(node);
    }

    @Override
    public boolean visit(IfStatement node) {
      ++complexity;
      return super.visit(node);
    }
  }
}
