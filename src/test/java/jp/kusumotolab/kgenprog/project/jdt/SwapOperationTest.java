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

public class SwapOperationTest {

  @Test
  public void testSwapStatement01() {
    final String source = ""
        + "class A {"
        + "  public int a() {"
        + "    int i = 0;"
        + "    int j = 1;"
        + "    int k = 2;"
        + "    return i + j;"
        + "  }"
        + "}";

    final ProductSourcePath sourcePath = new ProductSourcePath(Paths.get("A.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(sourcePath, source);
    @SuppressWarnings("unchecked") final GeneratedJDTAST<TestSourcePath> mockAst = Mockito.mock(
        GeneratedJDTAST.class);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast), Collections.singletonList(mockAst));

    // 入れ替え対象の位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement1 = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(sourcePath, statement1, ast);

    // 入れ替えに用いるNodeを生成
    final Statement statement2 = (Statement) method.getBody()
        .statements()
        .get(2);
    final SwapOperation operation = new SwapOperation(statement2);

    final GeneratedSourceCode appliedSourceCode = operation.apply(generatedSourceCode, location);
    final GeneratedJDTAST<ProductSourcePath> appliedAst =
        (GeneratedJDTAST<ProductSourcePath>) appliedSourceCode.getProductAsts()
            .get(0);

    final String expected = ""
        + "class A {"
        + "  public int a() {"
        + "    int k = 2;" // swapped with i
        + "    int j = 1;"
        + "    int i = 0;" // swapped with k
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
  public void testSwapStatement02() {
    final String source = ""
        + "class A {"
        + "  public int a() {"
        + "    int i = 0;"
        + "    int j = 1;"
        + "    int k = 2;"
        + "    return i + j;"
        + "  }"
        + "}";

    final ProductSourcePath sourcePath = new ProductSourcePath(Paths.get("A.java"));

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedJDTAST<ProductSourcePath> ast = constructor.constructAST(sourcePath, source);
    @SuppressWarnings("unchecked") final GeneratedJDTAST<TestSourcePath> mockAst = Mockito.mock(
        GeneratedJDTAST.class);
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast), Collections.singletonList(mockAst));

    // 入れ替え対象の位置のLocation生成
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement1 = (Statement) method.getBody()
        .statements()
        .get(2);
    final JDTASTLocation location = new JDTASTLocation(sourcePath, statement1, ast);

    // 入れ替えに用いるNodeを生成
    final Statement statement2 = (Statement) method.getBody()
        .statements()
        .get(0);
    final SwapOperation operation = new SwapOperation(statement2);

    final GeneratedSourceCode appliedSourceCode = operation.apply(generatedSourceCode, location);
    final GeneratedJDTAST<ProductSourcePath> appliedAst =
        (GeneratedJDTAST<ProductSourcePath>) appliedSourceCode.getProductAsts()
            .get(0);

    final String expected = ""
        + "class A {"
        + "  public int a() {"
        + "    int k = 2;" // swapped with i
        + "    int j = 1;"
        + "    int i = 0;" // swapped with k
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
