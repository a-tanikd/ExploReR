package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.Objects;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.ga.mutation.selection.ReuseCandidate;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

public class MethodName {

  private final FullyQualifiedName fqn;
  private final String methodName;

  public MethodName(final ReuseCandidate<Statement> candidate) {
    this(candidate.getFqn(), candidate.getValue());
  }

  public MethodName(final FullyQualifiedName fqn, final Statement statement) {
    this(fqn, getMethodName(statement));
  }

  private static String getMethodName(final Statement statement) {
    ASTNode currentNode = statement.getParent();
    while (currentNode.getNodeType() != ASTNode.METHOD_DECLARATION) {
      currentNode = currentNode.getParent();
    }

    return ((MethodDeclaration) currentNode).getName()
        .toString();
  }

  public MethodName(final FullyQualifiedName fqn, final String methodName) {
    this.fqn = fqn;
    this.methodName = methodName;
  }

  public FullyQualifiedName getFqn() {
    return fqn;
  }

  public String getMethodName() {
    return methodName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MethodName that = (MethodName) o;
    return getFqn().equals(that.getFqn()) &&
        getMethodName().equals(that.getMethodName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFqn(), getMethodName());
  }
}
