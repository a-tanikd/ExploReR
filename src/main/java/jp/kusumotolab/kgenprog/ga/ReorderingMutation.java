package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.jdt.SwapOperation;

public class ReorderingMutation extends Mutation {

  public ReorderingMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection) {
    super(mutationGeneratingCount, random, candidateSelection);
  }

  @Override
  public List<Variant> exec(final VariantStore variantStore) {

    final List<Variant> currentVariants = variantStore.getCurrentVariants();
    final Roulette<Variant> variantRoulette = new Roulette<>(currentVariants, e -> {
      final Fitness fitness = e.getFitness();
      final double value = fitness.getValue();
      return Double.isNaN(value) ? 0 : value + 1;
    }, random);

    final List<Variant> generatedVariants = new ArrayList<>();

    for (int i = 0; i < mutationGeneratingCount; i++) {
      final Variant variant = variantRoulette.exec();
      final List<Suspiciousness> suspiciousnesses = variant.getSuspiciousnesses();

      if (suspiciousnesses.isEmpty()) {
        continue;
      }

      final Function<Suspiciousness, Double> weightFunction = susp -> Math.pow(susp.getValue(), 2);
      final Roulette<Suspiciousness> suspiciousnessRoulette =
          new Roulette<>(suspiciousnesses, weightFunction, random);
      final Suspiciousness suspiciousness = suspiciousnessRoulette.exec();
      final Base base = new Base(suspiciousness.getLocation(),
          new SwapOperation(chooseNodeAtRandom()));
      final Gene gene = makeGene(variant.getGene(), base);
      final HistoricalElement element = new MutationHistoricalElement(variant, base);

      generatedVariants.add(variantStore.createVariant(gene, element));
    }

    return generatedVariants;
  }

  private ASTNode chooseNodeAtRandom() {
    return candidateSelection.exec();
  }

  private Gene makeGene(final Gene parent, final Base base) {
    final List<Base> bases = new ArrayList<>(parent.getBases());
    bases.add(base);
    return new Gene(bases);
  }
}
