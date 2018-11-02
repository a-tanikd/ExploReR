package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class NestingScannerTest {

  @Test
  public void testGetMetric() throws IOException {
    final Path path = Paths.get("example/QuickSort01/src/example/QuickSort.java");
    final List<String> lines = Files.readAllLines(path);
    final String sourceCode = lines.stream()
        .map(line -> line + "\n")
        .reduce("", String::concat);

    final CtClass clazz = Launcher.parseClass(sourceCode);
    final NestingScanner scanner = new NestingScanner();
    clazz.accept(scanner);

    assertThat(scanner.getMetric()).isEqualTo(2.0);
  }
}