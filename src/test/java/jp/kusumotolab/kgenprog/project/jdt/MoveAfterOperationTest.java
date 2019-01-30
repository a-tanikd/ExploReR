package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class MoveAfterOperationTest {

  @Test
  public void testMoveAfterStatement01() {
    final String source = ""
        + "class A {"
        + "  public int a() {"
        + "    int i = 0;"
        + "    int j = 1;"
        + "    int k = 2;"
        + "    return i + j;"
        + "  }"
        + "}";

    final ProductSourcePath sourcePath = new ProductSourcePath(Paths.get("."), Paths.get("A.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(sourcePath, source);
    @SuppressWarnings("unchecked") final GeneratedJDTAST<TestSourcePath> mockAst = Mockito.mock(
        GeneratedJDTAST.class);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast), Collections.singletonList(mockAst));

    // 移動先の位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement1 = (Statement) method.getBody()
        .statements()
        .get(0); // "int i = 0;"
    final JDTASTLocation location = new JDTASTLocation(sourcePath, statement1, ast);

    // 移動するNodeのLocation生成
    final Statement statement2 = (Statement) method.getBody()
        .statements()
        .get(2); // "int k = 2;"
    final JDTASTLocation ingredient = new JDTASTLocation(sourcePath, statement2, ast);
    final JDTOperation operation = new MoveAfterOperation(ingredient);

    final GeneratedSourceCode appliedSourceCode = operation.apply(generatedSourceCode, location);
    final GeneratedJDTAST<ProductSourcePath> appliedAst =
        (GeneratedJDTAST<ProductSourcePath>) appliedSourceCode.getProductAsts()
            .get(0);

    final String expected = ""
        + "class A {"
        + "  public int a() {"
        + "    int i = 0;"
        + "    int k = 2;" // moved after i
        + "    int j = 1;"
        + "    return i + j;"
        + "  }"
        + "}";

    assertThat(appliedAst.getRoot()).isSameSourceCodeAs(expected);

    // TestASTがそのまま受け継がれているか確認
    assertThat(appliedSourceCode.getTestAsts()).hasSize(1);
    assertThat(appliedSourceCode.getTestAsts()
        .get(0)).isSameAs(mockAst);
  }

  @Test
  public void testMoveAfterStatement02() {
    final String source = ""
        + "class A {"
        + "  public int a() {"
        + "    int i = 0;"
        + "    int j = 1;"
        + "    int k = 2;"
        + "    return i + j;"
        + "  }"
        + "}";

    final ProductSourcePath sourcePath = new ProductSourcePath(Paths.get("."), Paths.get("A.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(sourcePath, source);
    @SuppressWarnings("unchecked") final GeneratedJDTAST<TestSourcePath> mockAst = Mockito.mock(
        GeneratedJDTAST.class);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast), Collections.singletonList(mockAst));

    // 移動先の位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement1 = (Statement) method.getBody()
        .statements()
        .get(2); // int k = 2;
    final JDTASTLocation location = new JDTASTLocation(sourcePath, statement1, ast);

    // 移動するNodeのLocation生成
    final Statement statement2 = (Statement) method.getBody()
        .statements()
        .get(0); // int i = 0;
    final JDTASTLocation ingredient = new JDTASTLocation(sourcePath, statement2, ast);
    final JDTOperation operation = new MoveAfterOperation(ingredient);

    final GeneratedSourceCode appliedSourceCode = operation.apply(generatedSourceCode, location);
    final GeneratedJDTAST<ProductSourcePath> appliedAst =
        (GeneratedJDTAST<ProductSourcePath>) appliedSourceCode.getProductAsts()
            .get(0);

    final String expected = ""
        + "class A {"
        + "  public int a() {"
        + "    int j = 1;"
        + "    int k = 2;"
        + "    int i = 0;" // moved after k
        + "    return i + j;"
        + "  }"
        + "}";

    assertThat(appliedAst.getRoot()).isSameSourceCodeAs(expected);

    // TestASTがそのまま受け継がれているか確認
    assertThat(appliedSourceCode.getTestAsts()).hasSize(1);
    assertThat(appliedSourceCode.getTestAsts()
        .get(0)).isSameAs(mockAst);
  }
}
