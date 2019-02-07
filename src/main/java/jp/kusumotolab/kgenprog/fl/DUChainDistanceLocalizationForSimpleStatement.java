package jp.kusumotolab.kgenprog.fl;

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedMethodName;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class DUChainDistanceLocalizationForSimpleStatement extends DUChainDistanceLocalization {

  public DUChainDistanceLocalizationForSimpleStatement(
      final TargetFullyQualifiedMethodName refactoredMethodName) {
    super(refactoredMethodName);
  }

  @Override
  protected List<ASTLocation> filterLocations(List<ASTLocation> locations) {
    return locations.stream()
        .filter(location -> isSingleStatement(((JDTASTLocation) location).node))
        .collect(Collectors.toList());
  }

  private boolean isSingleStatement(ASTNode node) {
    switch (node.getNodeType()) {
      case ASTNode.ASSERT_STATEMENT:
      case ASTNode.BREAK_STATEMENT:
      case ASTNode.CONTINUE_STATEMENT:
      case ASTNode.EMPTY_STATEMENT:
      case ASTNode.EXPRESSION_STATEMENT:
      case ASTNode.LABELED_STATEMENT:
      case ASTNode.RETURN_STATEMENT:
      case ASTNode.SYNCHRONIZED_STATEMENT:
      case ASTNode.THROW_STATEMENT:
      case ASTNode.VARIABLE_DECLARATION_STATEMENT:
        return true;
      default:
        return false;
    }
  }
}
