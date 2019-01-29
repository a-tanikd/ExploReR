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
import org.eclipse.jdt.core.dom.SwitchStatement;
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

    final ASTNode declaringBlockLikeNode = declaringStatement.getParent();

    ASTNode current = getParentStatement(node);
    while (current.getParent() != declaringBlockLikeNode) {
      if (isSwitchStatement(current.getParent())) {
        distance += ((SwitchStatement) current.getParent()).statements()
            .indexOf(current) + 1;
      } else if (isSimpleBlock(current.getParent())) {
        final Block parentBlock = (Block) current.getParent();
        distance += parentBlock.statements()
            .indexOf(current) + 1;
      } else if (isBlockStatement(current.getParent())) {
        final Block parentBlock = (Block) current.getParent();
        distance += parentBlock.statements()
            .indexOf(current) + 1;
        current = current.getParent();
      } else {
        distance += 1;
      }
      current = current.getParent();
    }

    final int declarationIndex = indexOfIn(declaringStatement, declaringBlockLikeNode);
    final int referenceIndex = indexOfIn(current, declaringBlockLikeNode);

    if (declarationIndex == -1 || referenceIndex == -1) {
      throw new RuntimeException();
    }

    distance += referenceIndex - declarationIndex;

    return false;
  }

  private int indexOfIn(ASTNode of, ASTNode in) {
    if (isSwitchStatement(in)) {
      return ((SwitchStatement) in).statements()
          .indexOf(of);
    } else if (in.getNodeType() == ASTNode.BLOCK) {
      return ((Block) in).statements()
          .indexOf(of);
    } else {
      throw new IllegalStateException("this node does not have statement list: " + in.toString());
    }
  }

  private Statement getDeclaringStatement(IVariableBinding binding) {
    ASTNode current = compilationUnit.findDeclaringNode(binding);
    if (current.getNodeType() == ASTNode.SINGLE_VARIABLE_DECLARATION) {
      return null;
    }

    while (current.getNodeType() != ASTNode.VARIABLE_DECLARATION_STATEMENT) {
      if (current.getNodeType() == ASTNode.COMPILATION_UNIT) {
        return null;
      }
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

  private boolean isSwitchStatement(ASTNode node) {
    return node.getNodeType() == ASTNode.SWITCH_STATEMENT;
  }

  private boolean isSimpleBlock(final ASTNode node) {
    return isBlockStatement(node) && isBlockStatement(node.getParent());
  }

  private boolean isBlockStatement(ASTNode node) {
    return node.getNodeType() == ASTNode.BLOCK;
  }

  private boolean isSimpleStatement(ASTNode node) {
    switch (node.getNodeType()) {
      case ASTNode.ASSERT_STATEMENT:
      case ASTNode.BREAK_STATEMENT:
      case ASTNode.CONTINUE_STATEMENT:
      case ASTNode.EMPTY_STATEMENT:
      case ASTNode.EXPRESSION_STATEMENT:
      case ASTNode.RETURN_STATEMENT:
      case ASTNode.THROW_STATEMENT:
      case ASTNode.VARIABLE_DECLARATION_STATEMENT:
        return true;
      default:
        return false;
    }
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

//  private List<Statement> getStatements(final ASTNode complexStatement) {
//    if (isSwitchStatement(complexStatement)) {
//      return ((SwitchStatement) complexStatement).statements();
//    }
//
//    if (isDoStatement(complexStatement)) {
//      final Statement body = ((DoStatement) complexStatement).getBody();
//      return getStatementsFromBody(body);
//    }
//
//    if (isEnhancedForStatement(complexStatement)) {
//      final Statement body = ((EnhancedForStatement) complexStatement).getBody();
//      return getStatementsFromBody(body);
//    }
//
//    if (isForStatemen(complexStatement)) {
//      final Statement body = ((ForStatement) complexStatement).getBody();
//      return getStatementsFromBody(body);
//    }
//
//    if (isIfStatement(complexStatement)) {
//      final Statement body = ((IfStatement) complexStatement).getThenStatement();
//      return getStatementsFromBody(body);
//    }
//
//    if (isTryStatement(complexStatement)) {
//      final Statement body = ((TryStatement) complexStatement).getBody();
//      return getStatementsFromBody(body);
//    }
//
//    if (isWhileStatement(complexStatement)) {
//      final Statement body = ((WhileStatement) complexStatement).getBody();
//      return getStatementsFromBody(body);
//    }
//
//    throw new IllegalStateException(
//        "this node does not have statements: " + complexStatement.toString());
//  }
//
//  private List<Statement> getStatementsFromBody(Statement body) {
//    return isBlockStatement(body) ? ((Block) body).statements()
//        : ImmutableList.of(body);
//  }
//
//  private boolean isDoStatement(final ASTNode node) {
//    return node.getNodeType() == ASTNode.DO_STATEMENT;
//  }
//
//  private boolean isEnhancedForStatement(final ASTNode node) {
//    return node.getNodeType() == ASTNode.ENHANCED_FOR_STATEMENT;
//  }
//
//  private boolean isForStatemen(final ASTNode node) {
//    return node.getNodeType() == ASTNode.FOR_STATEMENT;
//  }
//
//  private boolean isIfStatement(final ASTNode node) {
//    return node.getNodeType() == ASTNode.IF_STATEMENT;
//  }
//
//  private boolean isTryStatement(final ASTNode node) {
//    return node.getNodeType() == ASTNode.TRY_STATEMENT;
//  }
//
//  private boolean isWhileStatement(final ASTNode node) {
//    return node.getNodeType() == ASTNode.WHILE_STATEMENT;
//  }
}
