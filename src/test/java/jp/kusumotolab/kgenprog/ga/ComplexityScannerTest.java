package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class ComplexityScannerTest {

  @Test
  public void testGetMetric() throws IOException {
    final Path path = Paths.get("example/QuickSort01/src/example/QuickSort.java");
    final List<String> lines = Files.readAllLines(path);
    final String sourceCode = lines.stream()
        .map(line -> line + "\n")
        .reduce("", String::concat);

    // calculate sum of Cyclomatic Complexity of each method in each class
    final CtClass clazz = Launcher.parseClass(sourceCode);
    final ComplexityScanner scanner = new ComplexityScanner();
    clazz.accept(scanner);

    assertThat(scanner.getMetric()).isEqualTo(9.0);
  }
}
