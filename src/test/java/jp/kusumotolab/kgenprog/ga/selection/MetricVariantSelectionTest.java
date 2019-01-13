package jp.kusumotolab.kgenprog.ga.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;
import org.mockito.Mockito;
import com.google.common.collect.Lists;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.MetricFitness;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class MetricVariantSelectionTest {

  @Test
  public void testExec() {
    final int headcount = 5;
    final AscendingVariantSelection variantSelection = new AscendingVariantSelection(headcount);
    final List<Variant> variants = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      final double divider = (i % 2 == 0) ? 10 : 20;
      final double value = (double) i / divider;
      final Fitness fitness = new MetricFitness(value, 1.0d);
      variants.add(createVariant(fitness));
    }
    assertThat(variants).hasSize(10)
        .extracting(Variant::getFitness)
        .extracting(Fitness::getValue)
        .hasSize(10)
        .containsExactly(0.00d, 0.05d, 0.20d, 0.15d, 0.40d, 0.25d, 0.60d, 0.35d, 0.80d, 0.45d);

    final List<Variant> selectedVariants = variantSelection.exec(Collections.emptyList(), variants);
    assertThat(selectedVariants).hasSize(headcount)
        .extracting(Variant::getFitness)
        .extracting(Fitness::getValue)
        .containsExactly(0.00d, 0.05d, 0.15d, 0.20d, 0.25d);
  }

  @Test
  public void testExecForEmptyVariants() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10);
    final List<Variant> variants1 = Collections.emptyList();
    final List<Variant> variants2 = Collections.emptyList();
    final List<Variant> resultVariants = variantSelection.exec(variants1, variants2);
    assertThat(resultVariants).hasSize(0);
  }

  @Test
  public void testExecForNan() {
    final int headcount = 10;
    final AscendingVariantSelection variantSelection = new AscendingVariantSelection(headcount);

    final List<Variant> nanVariants = IntStream.range(0, 20)
        .mapToObj(e -> new MetricFitness(Double.NaN, 0d))
        .map(this::createVariant)
        .collect(Collectors.toList());

    final List<Variant> result1 = variantSelection.exec(Collections.emptyList(), nanVariants);
    assertThat(result1).hasSize(headcount);

    final List<Variant> variants = Lists.newArrayList(nanVariants);
    final Variant normalVariant = createVariant(new MetricFitness(0.5d, 1.0d));
    variants.add(normalVariant);
    final List<Variant> result2 = variantSelection.exec(Collections.emptyList(), variants);
    assertThat(result2).hasSize(headcount);
    assertThat(result2.get(0)).isEqualTo(normalVariant);
  }

  @Test
  public void testExecForNanCompare() {
    int headcount = 10;
    final AscendingVariantSelection variantSelection = new AscendingVariantSelection(headcount);

    final List<Variant> nanVariants = IntStream.range(0, 100)
        .mapToObj(e -> {
          if (e == 50) {
            return new MetricFitness(5, 1.0d);
          }
          return new MetricFitness(Double.NaN, 0.0d);
        })
        .map(this::createVariant)
        .collect(Collectors.toList());

    try {
      final List<Variant> result = variantSelection.exec(Collections.emptyList(), nanVariants);
      assertThat(result).hasSize(headcount);
      assertThat(result.get(0)
          .getFitness()
          .getValue())
          .isCloseTo(5, within(0.001));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testExecForBuildFailedVariant() {
    final List<Variant> variants = Stream.iterate(new MetricFitness(5, 1.0d),
        UnaryOperator.identity())
        .map(this::createVariant)
        .limit(9)
        .collect(Collectors.toList());
    variants.add(createVariant(new MetricFitness(5, 1.0d), true, false));

    assertThat(variants).hasSize(10)
        .extracting(Variant::getFitness)
        .allSatisfy(fitness -> assertThat(fitness.getValue()).isCloseTo(5, within(0.001d)));

    final int headcount = 10;
    final VariantSelection selection = new MetricVariantSelection(headcount);
    final List<Variant> selectedVariants = selection.exec(Collections.emptyList(), variants);
    assertThat(selectedVariants).hasSize(headcount)
        .last()
        .satisfies(variant -> assertThat(variant.isBuildSucceeded()).isFalse());
    assertThat(selectedVariants.subList(0, 9)).allSatisfy(Variant::isBuildSucceeded);
  }

  @Test
  public void testExecForSyntaxInvalidVariant() {
    final List<Variant> variants = Stream.iterate(new MetricFitness(5, 1.0d),
        UnaryOperator.identity())
        .map(this::createVariant)
        .limit(9)
        .collect(Collectors.toList());
    variants.add(createVariant(new MetricFitness(5, 1.0d), false, false));

    assertThat(variants).hasSize(10)
        .extracting(Variant::getFitness)
        .allSatisfy(fitness -> assertThat(fitness.getValue()).isCloseTo(5, within(0.001d)));

    final int headcount = 10;
    final VariantSelection selection = new MetricVariantSelection(headcount);
    final List<Variant> selectedVariants = selection.exec(Collections.emptyList(), variants);
    assertThat(selectedVariants).hasSize(headcount)
        .last()
        .satisfies(variant -> assertThat(variant.isBuildSucceeded()).isFalse());
    assertThat(selectedVariants.subList(0, 9)).allSatisfy(Variant::isBuildSucceeded);
  }

  @Test
  public void testExecForBuildFailedAndSyntaxInvalidVariant() {
    final List<Variant> variants = Stream.iterate(new MetricFitness(5, 1.0d),
        UnaryOperator.identity())
        .map(this::createVariant)
        .limit(8)
        .collect(Collectors.toList());
    variants.add(createVariant(new MetricFitness(5, 1.0d), true, false));
    variants.add(createVariant(new MetricFitness(5, 1.0d), false, false));

    assertThat(variants).hasSize(10)
        .extracting(Variant::getFitness)
        .allSatisfy(fitness -> assertThat(fitness.getValue()).isCloseTo(5, within(0.001d)));

    final int headcount = 10;
    final VariantSelection selection = new MetricVariantSelection(headcount);
    final List<Variant> selectedVariants = selection.exec(Collections.emptyList(), variants);
    assertThat(selectedVariants).hasSize(headcount);
    assertThat(selectedVariants.get(8)).satisfies(variant -> {
      assertThat(variant.isSyntaxValid()).isTrue();
      assertThat(variant.isBuildSucceeded()).isFalse();
    });
    assertThat(selectedVariants.get(9)).satisfies(variant -> {
      assertThat(variant.isSyntaxValid()).isFalse();
      assertThat(variant.isBuildSucceeded()).isFalse();
    });
    assertThat(selectedVariants.subList(0, 9)).allSatisfy(Variant::isBuildSucceeded);
  }

  private Variant createVariant(final Fitness fitness) {
    return createVariant(fitness, true, true);
  }

  private Variant createVariant(final Fitness fitness, final boolean isSyntaxValid,
      final boolean isBuildSucceeded) {

    final Variant mockVariant = Mockito.mock(Variant.class);
    Mockito.when(mockVariant.getFitness())
        .thenReturn(fitness);
    Mockito.when(mockVariant.isBuildSucceeded())
        .thenReturn(isBuildSucceeded);
    Mockito.when(mockVariant.isSyntaxValid())
        .thenReturn(isSyntaxValid);

    return mockVariant;
  }
}
