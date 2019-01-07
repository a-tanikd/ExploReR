package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class SwapOperation extends JDTOperation {

  private final ASTNode astNode;

  public SwapOperation(final ASTNode astNode) {
    this.astNode = astNode;
  }

  @Override
  protected <T extends SourcePath> void applyToASTRewrite(final GeneratedJDTAST<T> ast,
      final JDTASTLocation location, final ASTRewrite astRewrite) {

    // astNode の Location 作成
    final JDTASTLocation location2 = new JDTASTLocation(location.getSourcePath(), astNode, null);

    final ASTNode target1 = location.locate(ast.getRoot());
    final ASTNode target2 = location2.locate(ast.getRoot());

    final ASTNode copiedTarget1 = astRewrite.createCopyTarget(target1);
    final ASTNode copiedTarget2 = astRewrite.createCopyTarget(target2);

    final ListRewrite listRewrite = astRewrite.getListRewrite(target1.getParent(),
        (ChildListPropertyDescriptor) target1.getLocationInParent());

    astRewrite.replace(target1, copiedTarget2, null);
    astRewrite.replace(target2, copiedTarget1, null);
  }
}
