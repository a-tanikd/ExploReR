package jp.kusumotolab.kgenprog.analysis;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

public class ComplexityVisitor extends ASTVisitor {

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


