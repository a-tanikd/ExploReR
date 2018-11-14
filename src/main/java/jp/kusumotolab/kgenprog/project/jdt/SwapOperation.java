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

    final ASTNode target = location.locate(ast.getRoot());
    final ListRewrite listRewrite = astRewrite.getListRewrite(target.getParent(),
        (ChildListPropertyDescriptor) target.getLocationInParent());

    int index1 = listRewrite.getOriginalList()
        .indexOf(target);
    int index2 = listRewrite.getOriginalList()
        .indexOf(astNode);
    listRewrite.remove(astNode, null);
    listRewrite.remove(target, null);
    listRewrite.insertAt(astNode, index1, null);
    listRewrite.insertAt(target, index2 + 1, null);
  }
}
