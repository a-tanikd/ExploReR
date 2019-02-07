package jp.kusumotolab.kgenprog.ga.selection;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class MetricVariantSelection implements VariantSelection {

  final private int maxVariantsPerGeneration;

  public MetricVariantSelection(int maxVariantPerGeneration) {
    this.maxVariantsPerGeneration = maxVariantPerGeneration;
  }

  @Override
  public List<Variant> exec(final List<Variant> current, final List<Variant> generated) {
    final Comparator<Variant> comparator = Comparator.comparing(Variant::isBuildSucceeded,
        Comparator.reverseOrder())
        .thenComparing(Variant::isSyntaxValid, Comparator.reverseOrder())
        .thenComparing(Variant::getFitness);
    return Stream.concat(current.stream(), generated.stream())
        .filter(Variant::isBuildSucceeded)
        .sorted(comparator)
        .limit(maxVariantsPerGeneration)
        .collect(Collectors.toList());
  }
}
