package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class MoveAfterOperation extends JDTOperation {

  private static final Logger log = LoggerFactory.getLogger(MoveAfterOperation.class);

  private final JDTASTLocation ingredient;

  public MoveAfterOperation(final JDTASTLocation ingredient) {
    this.ingredient = ingredient;
  }

  @Override
  protected <T extends SourcePath> void applyToASTRewrite(final GeneratedJDTAST<T> ast,
      final JDTASTLocation location, final ASTRewrite astRewrite) {

    final ASTNode target1 = location.locate(ast.getRoot());
    final ASTNode target2 = ingredient.locate(ast.getRoot());

    log.debug("src : {} -> {}", ingredient.node.toString(), target2.toString());
    log.debug("dest: {} -> {}", location.node.toString(), target1.toString());

    final ASTNode copiedTarget2 = ASTNode.copySubtree(astRewrite.getAST(), target2);

    final ListRewrite listRewrite = astRewrite.getListRewrite(target1.getParent(),
        (ChildListPropertyDescriptor) target1.getLocationInParent());

    listRewrite.insertAfter(copiedTarget2, target1, null);
    astRewrite.remove(target2, null);
  }

}
