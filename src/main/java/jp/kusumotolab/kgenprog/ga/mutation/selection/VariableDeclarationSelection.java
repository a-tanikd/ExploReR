package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.Random;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;

public class VariableDeclarationSelection extends StatementSelection {

  public VariableDeclarationSelection(final Random random) {
    super(random);
  }

  @Override
  public double getStatementWeight(final ReuseCandidate<Statement> reuseCandidate) {
    final Statement statement = reuseCandidate.getValue();
    return isVariableDeclarationStatement(statement) ? 1.0d : 0.0d;
  }

  private boolean isVariableDeclarationStatement(Statement statement) {
    return statement.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT;
  }
}
