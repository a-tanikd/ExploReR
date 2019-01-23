package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class MoveAfterOperation extends JDTOperation {

  private final ASTNode astNode;

  public MoveAfterOperation(final ASTNode astNode) {
    this.astNode = astNode;
  }

  @Override
  protected <T extends SourcePath> void applyToASTRewrite(final GeneratedJDTAST<T> ast,
      final JDTASTLocation location, final ASTRewrite astRewrite) {

    // astNode の Location 作成
    final JDTASTLocation location2 = new JDTASTLocation(location.getSourcePath(), astNode, null);

    final ASTNode target1 = location.locate(ast.getRoot());
    final ASTNode target2 = location2.locate(ast.getRoot());

    final ASTNode copiedTarget2 = ASTNode.copySubtree(astRewrite.getAST(), astNode);

    final ListRewrite listRewrite = astRewrite.getListRewrite(target1.getParent(),
        (ChildListPropertyDescriptor) target1.getLocationInParent());

    listRewrite.insertAfter(copiedTarget2, target1, null);
    astRewrite.remove(target2, null);
  }

  @Override
  public String getName() {
    return "move after";
  }
}
