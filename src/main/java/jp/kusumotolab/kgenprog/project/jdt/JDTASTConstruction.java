package jp.kusumotolab.kgenprog.project.jdt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import com.google.common.collect.ImmutableList;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class JDTASTConstruction {

  public GeneratedSourceCode constructAST(final TargetProject project) {
    return constructAST(false, project);
  }

  public GeneratedSourceCode constructAST(boolean needBinding, final TargetProject project) {
    return constructAST(needBinding, project.getProductSourcePaths(), project.getTestSourcePaths(),
        project.getClassPaths());
  }

  public GeneratedSourceCode constructAST(final List<ProductSourcePath> productSourcePaths,
      final List<TestSourcePath> testSourcePaths) {

    return constructAST(false, productSourcePaths, testSourcePaths, null);
  }

  public GeneratedSourceCode constructAST(boolean needBinding,
      final List<ProductSourcePath> productSourcePaths,
      final List<TestSourcePath> testSourcePaths, List<ClassPath> classPaths) {
    final String[] paths = Stream.concat(productSourcePaths.stream(), testSourcePaths.stream())
        .map(path -> path.path.toString())
        .toArray(String[]::new);

    final ASTParser parser = createNewParser(needBinding, productSourcePaths, classPaths);

    final Map<Path, ProductSourcePath> pathToProductSourcePath = productSourcePaths.stream()
        .collect(Collectors.toMap(path -> path.path, path -> path));
    final Map<Path, TestSourcePath> pathToTestSourcePath = testSourcePaths.stream()
        .collect(Collectors.toMap(path -> path.path, path -> path));

    final List<GeneratedAST<ProductSourcePath>> productAsts = new ArrayList<>();
    final List<GeneratedAST<TestSourcePath>> testAsts = new ArrayList<>();
    final List<IProblem> problems = new ArrayList<>();

    final FileASTRequestor requestor = new FileASTRequestor() {

      @Override
      public void acceptAST(final String sourcePath, final CompilationUnit ast) {
        final ProductSourcePath productPath = pathToProductSourcePath.get(Paths.get(sourcePath));
        if (productPath != null) {
          productAsts.add(new GeneratedJDTAST<>(JDTASTConstruction.this, productPath, ast,
              loadAsString(sourcePath)));
        }

        final TestSourcePath testPath = pathToTestSourcePath.get(Paths.get(sourcePath));
        if (testPath != null) {
          testAsts.add(new GeneratedJDTAST<>(JDTASTConstruction.this, testPath, ast,
              loadAsString(sourcePath)));
        }

        problems.addAll(Arrays.asList(ast.getProblems()));
      }
    };

    parser.createASTs(paths, null, new String[] {}, requestor, null);

    if (isConstructionSuccess(problems)) {
      return new GeneratedSourceCode(productAsts, testAsts);
    } else {
      final String messages = concatProblemMessages(problems);
      return new GenerationFailedSourceCode(messages);
    }
  }

  public <T extends SourcePath> GeneratedJDTAST<T> constructAST(final T sourcePath,
      final String data) {
    // todo: needBinding を Config に吸い上げる
    final ASTParser parser = createNewParser(true, ImmutableList.of(sourcePath), null);
    parser.setSource(data.toCharArray());
    parser.setUnitName(sourcePath.toString());
    final CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);

    return new GeneratedJDTAST<>(this, sourcePath, compilationUnit, data);
  }

  public static ASTParser createNewParser() {
    return createNewParser(false, null, null);
  }

  public static <T extends SourcePath> ASTParser createNewParser(boolean needBinding,
      List<T> sourcePaths, List<ClassPath> classPaths) {
    final ASTParser parser = ASTParser.newParser(AST.JLS10);

    @SuppressWarnings("unchecked") final Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
    options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
    options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
    parser.setCompilerOptions(options);

    // TODO: Bindingが必要か検討
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setResolveBindings(needBinding);
    parser.setBindingsRecovery(needBinding);

    final String[] classPathsEntries =
        classPaths == null ? null : classPaths.stream()
            .map(classPath -> classPath.path.toString())
            .toArray(String[]::new);
    final String[] sourcePathEntries =
        sourcePaths == null ? null : sourcePaths.stream()
            .map(sourcePath -> sourcePath.path)
            .map(path -> {
              if (Files.isDirectory(path)) {
                return path.toString();
              }
              if (path.getParent() == null) {
                return Paths.get("")
                    .toString();
              }
              return path.getParent()
                  .toString();
            })
            .toArray(String[]::new);
    parser.setEnvironment(classPathsEntries, sourcePathEntries, null, true);
//    parser.setEnvironment(null, null, null, true);

    return parser;
  }

  private String loadAsString(final String path) {
    try {
      return new String(Files.readAllBytes(Paths.get(path)));
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isConstructionSuccess(final List<IProblem> problems) {
    return problems.stream()
        .noneMatch(IProblem::isError);
  }

  private String concatProblemMessages(final List<IProblem> problems) {
    return problems.stream()
        .map(IProblem::getMessage)
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
