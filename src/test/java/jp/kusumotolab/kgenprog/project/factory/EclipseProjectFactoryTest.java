package jp.kusumotolab.kgenprog.project.factory;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class EclipseProjectFactoryTest {
  private final static ClassPath Junit = new ClassPath(Paths.get("example/BuildSuccess06/lib/junit-4.12.jar"));

  @Test
  public void testCreateBySingleEclipseProject() {
    final Path rootPath = Paths.get("example/BuildSuccess06");
    final EclipseProjectFactory eclipseProjectFactory = new EclipseProjectFactory(rootPath);
    final TargetProject project = eclipseProjectFactory.create();

    final ProductSourcePath foo = new ProductSourcePath(rootPath.resolve("src/Foo.java"));
    final TestSourcePath fooTest = new TestSourcePath(rootPath.resolve("src/FooTest.java"));

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(foo);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTest);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(Junit);
  }

}
