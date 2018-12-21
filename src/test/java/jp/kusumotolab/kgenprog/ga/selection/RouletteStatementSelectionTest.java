package jp.kusumotolab.kgenprog.ga.selection;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import org.eclipse.jdt.core.dom.Statement;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Roulette;
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.ReuseCandidate;
import jp.kusumotolab.kgenprog.ga.mutation.selection.RouletteStatementSelection;
import jp.kusumotolab.kgenprog.ga.mutation.selection.StatementSelection;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class RouletteStatementSelectionTest {

  @Test
  public void testProjectScope() {
    final StatementSelection statementSelection = createStatementSelection();
    final Roulette<ReuseCandidate<Statement>> roulette = statementSelection.getRoulette(
        new Scope(Type.PROJECT, (FullyQualifiedName) null));
    assertThat(roulette.getCandidateList()).hasSize(3);
  }

  @Test
  public void testPackageScope() {
    final StatementSelection statementSelection = createStatementSelection();
    final Roulette<ReuseCandidate<Statement>> roulette = statementSelection.getRoulette(
        new Scope(Type.PACKAGE, new TargetFullyQualifiedName("example.Foo")));
    assertThat(roulette.getCandidateList()).hasSize(2);
  }

  @Test
  public void testFileScope() {
    final StatementSelection statementSelection = createStatementSelection();
    final Roulette<ReuseCandidate<Statement>> roulette = statementSelection.getRoulette(
        new Scope(Type.FILE, new TargetFullyQualifiedName("example.Foo")));
    assertThat(roulette.getCandidateList()).hasSize(1);
  }

  @Test
  public void testMethodScope() {
    final StatementSelection statementSelection = createStatementSelection("example/refactoring/GeometricMean");
    final TargetFullyQualifiedName fqn = new TargetFullyQualifiedName("example.GeometricMean");
    final Roulette<ReuseCandidate<Statement>> roulette = statementSelection.getRoulette(
        new Scope(Type.METHOD, fqn, "geometricMean"));
    assertThat(roulette.getCandidateList()).hasSize(4);
  }

  private StatementSelection createStatementSelection() {
    return createStatementSelection("example/BuildSuccess15");
  }

  private StatementSelection createStatementSelection(final String projectRoot) {
    final Path basePath = Paths.get(projectRoot);
    final TargetProject targetProject = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode sourceCode= TestUtil.createGeneratedSourceCode(
        targetProject);
    final List<GeneratedAST<ProductSourcePath>> asts = sourceCode.getProductAsts();

    final Random random = new Random(0);
    final StatementSelection statementSelection = new RouletteStatementSelection(random);
    statementSelection.setCandidates(asts);
    return statementSelection;
  }
}
