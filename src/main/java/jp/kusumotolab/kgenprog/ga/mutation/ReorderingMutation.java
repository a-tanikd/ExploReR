package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.Roulette;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.jdt.JDTOperation;
import jp.kusumotolab.kgenprog.project.jdt.MoveAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.MoveBeforeOperation;

public class ReorderingMutation extends Mutation {

  public ReorderingMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection) {
    super(mutationGeneratingCount, random, candidateSelection, Type.METHOD);
  }

  @Override
  public List<Variant> exec(final VariantStore variantStore) {

    final List<Variant> currentVariants = variantStore.getCurrentVariants();
    final Roulette<Variant> variantRoulette = new Roulette<>(currentVariants, variant -> {
      final Fitness fitness = variant.getFitness();
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

      setCandidates(variant.getGeneratedSourceCode()
          .getProductAsts());

      final Suspiciousness suspiciousness = selectSuspiciousness(suspiciousnesses);
      final Base base = makeBase((JDTASTLocation) suspiciousness.getLocation());
      final Gene gene = makeGene(variant.getGene(), base);
      final HistoricalElement element = new MutationHistoricalElement(variant, base);

      generatedVariants.add(variantStore.createVariant(gene, element));
    }

    return generatedVariants;
  }

  private Suspiciousness selectSuspiciousness(final List<Suspiciousness> suspiciousnesses) {
//    final Function<Suspiciousness, Double> weightFunction = susp -> Math.pow(susp.getValue(), 2);
//    final Roulette<Suspiciousness> suspiciousnessRoulette =
//        new Roulette<>(suspiciousnesses, weightFunction, random);
//
//    return suspiciousnessRoulette.exec();

    return suspiciousnesses.get(random.nextInt(suspiciousnesses.size()));
  }

  private Base makeBase(JDTASTLocation location) {
    final JDTASTLocation srcLocation = selectIngredientLocation(location);
    final JDTOperation operation = makeOperationRandomly(srcLocation);

    return new Base(location, operation);
  }

  private JDTOperation makeOperationRandomly(JDTASTLocation srcLocation) {
    final int randomNumber = random.nextInt(2);
    switch (randomNumber) {
      case 0:
        return new MoveBeforeOperation(srcLocation);
      case 1:
        return new MoveAfterOperation(srcLocation);
      default:
        throw new IllegalStateException("cannot make Operation");
    }

  }

  private JDTASTLocation selectIngredientLocation(final JDTASTLocation location) {
    final GeneratedJDTAST<?> generatedAST = ((GeneratedJDTAST) location.getGeneratedAST());
    final FullyQualifiedName fqn = generatedAST.getPrimaryClassName();
    final Statement statement = (Statement) location.node;
    final MethodName methodName = new MethodName(fqn, statement);

    if (methodName.getMethodName()
        .isEmpty()) {
      throw new IllegalStateException("cannot retrieve reuse candidate.");
    }

    final Scope scope = new Scope(type, methodName);

    return new JDTASTLocation(location.getSourcePath(), candidateSelection.exec(scope),
        generatedAST);
  }

  private Gene makeGene(final Gene parent, final Base base) {
    final List<Base> bases = new ArrayList<>(parent.getBases());
    bases.add(base);
    return new Gene(bases);
  }
}
