package jp.kusumotolab.kgenprog.analysis;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class DUChainDistanceVisitor extends ASTVisitor {

  private int distance = 0;
  private final Map<Integer, Statement> lineToStatement = new HashMap<>();
  private CompilationUnit compilationUnit;

  public DUChainDistanceVisitor(final CompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
  }

  public int getDistance() {
    return distance;
  }

  @Override
  public boolean visit(VariableDeclarationStatement node) {
    final int startLine = compilationUnit.getLineNumber(node.getStartPosition());
    final int endLine = compilationUnit.getLineNumber(
        node.getStartPosition() + node.getLength() - 1);

    for (int i = startLine; i <= endLine; ++i) {
      lineToStatement.put(i, node);
    }

    return true;
  }

  @Override
  public boolean visit(SimpleName node) {

    final IBinding binding = node.resolveBinding();
    if (binding == null) {
      return false;
    }
    if (binding.getKind() != IBinding.VARIABLE) {
      return false;
    }

    final IVariableBinding variableBinding = (IVariableBinding) binding;
    if (variableBinding.getDeclaringMethod() == null) {
      return false;
    }
    if (variableBinding.isParameter()) {
      return false;
    }

    if (node.getStartPosition() == -1) {
      return false;
    }

    final Statement declaringStatement = getDeclaringStatement(variableBinding);
    if (declaringStatement == null) {
      return false;
    }

    final Block declaringBlock = (Block) declaringStatement.getParent();

    ASTNode current = getParentStatement(node);
    while (current.getParent() != declaringBlock) {
      if (current.getNodeType() != ASTNode.BLOCK) {
        final Block parentBlock = (Block) current.getParent();
        distance += parentBlock.statements().indexOf(current) + 1;
      }
      current = current.getParent();
    }

    final int declarationIndex = declaringBlock.statements()
        .indexOf(declaringStatement);
    final int referenceIndex = declaringBlock.statements()
        .indexOf(current);

    if (declarationIndex == -1 || referenceIndex == -1) {
      throw new RuntimeException();
    }

    distance += referenceIndex - declarationIndex;

    return false;
  }

  private Statement getDeclaringStatement(IVariableBinding binding) {
    ASTNode current = compilationUnit.findDeclaringNode(binding);
    if (current.getNodeType() == ASTNode.SINGLE_VARIABLE_DECLARATION) {
      return null;
    }

    while (current.getNodeType() != ASTNode.VARIABLE_DECLARATION_STATEMENT) {
      current = current.getParent();
    }
    return (Statement) current;
  }

  private Statement getParentStatement(SimpleName node) {
    ASTNode parent = node.getParent();

    while (!isStatement(parent)) {
      parent = parent.getParent();
    }
    return (Statement) parent;
  }

  private boolean isStatement(ASTNode node) {
    switch (node.getNodeType()) {
      case ASTNode.ASSERT_STATEMENT:
      case ASTNode.BREAK_STATEMENT:
      case ASTNode.CONTINUE_STATEMENT:
      case ASTNode.DO_STATEMENT:
      case ASTNode.EMPTY_STATEMENT:
      case ASTNode.ENHANCED_FOR_STATEMENT:
      case ASTNode.EXPRESSION_STATEMENT:
      case ASTNode.FOR_STATEMENT:
      case ASTNode.IF_STATEMENT:
      case ASTNode.LABELED_STATEMENT:
      case ASTNode.RETURN_STATEMENT:
      case ASTNode.SWITCH_STATEMENT:
      case ASTNode.SYNCHRONIZED_STATEMENT:
      case ASTNode.THROW_STATEMENT:
      case ASTNode.TRY_STATEMENT:
      case ASTNode.WHILE_STATEMENT:
      case ASTNode.VARIABLE_DECLARATION_STATEMENT:
        return true;
      default:
        return false;
    }
  }

}
