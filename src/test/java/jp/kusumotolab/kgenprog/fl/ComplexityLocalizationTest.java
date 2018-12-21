package jp.kusumotolab.kgenprog.fl;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class ComplexityLocalizationTest {

  @Test
  public void testForExample01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final FaultLocalization fl = new ComplexityLocalization();
    final List<Suspiciousness> suspiciousnesses =
        fl.exec(initialVariant.getGeneratedSourceCode(), initialVariant.getTestResults());

    assertThat(suspiciousnesses).extracting(Suspiciousness::getValue)
        .containsExactlyInAnyOrder(1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0);
  }

  @Test
  public void testForExample02() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final FaultLocalization fl = new ComplexityLocalization();
    final List<Suspiciousness> suspiciousnesses =
        fl.exec(initialVariant.getGeneratedSourceCode(), initialVariant.getTestResults());

    assertThat(suspiciousnesses).extracting(Suspiciousness::getValue)
        .containsExactlyInAnyOrder(1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
  }

  @Test
  public void testForGeometricMean() {
    final Path rootPath = Paths.get("example/refactoring/GeometricMean");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(targetProject).build();
    final Variant initialVariant = TestUtil.createVariant(config);

    final FaultLocalization fl = new ComplexityLocalization();
    final List<Suspiciousness> suspiciousnesses =
        fl.exec(initialVariant.getGeneratedSourceCode(), initialVariant.getTestResults());

    assertThat(suspiciousnesses).extracting(Suspiciousness::getValue)
        .containsExactlyInAnyOrder(1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
  }
}
