package jp.kusumotolab.kgenprog.ga.selection;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class AscendingVariantSelection implements VariantSelection {

  final private int maxVariantsPerGeneration;

  public AscendingVariantSelection(int maxVariantPerGeneration) {
    this.maxVariantsPerGeneration = maxVariantPerGeneration;
  }

  @Override
  public List<Variant> exec(final List<Variant> current, final List<Variant> generated) {
    final List<Variant> list = Stream.concat(current.stream(), generated.stream())
        .sorted(Comparator.comparing(Variant::getFitness))
        .limit(maxVariantsPerGeneration)
        .collect(Collectors.toList());
    return list;
  }
}
