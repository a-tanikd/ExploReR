package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.eclipse.jdt.core.dom.ASTNode;
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
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.jdt.JDTOperation;
import jp.kusumotolab.kgenprog.project.jdt.MoveAfterOperation;

public class ReorderingMutation extends Mutation {

  public ReorderingMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection) {
    super(mutationGeneratingCount, random, candidateSelection, Type.METHOD);
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

      setCandidates(variant.getGeneratedSourceCode()
          .getProductAsts());

      final Function<Suspiciousness, Double> weightFunction = susp -> Math.pow(susp.getValue(), 2);
      final Roulette<Suspiciousness> suspiciousnessRoulette =
          new Roulette<>(suspiciousnesses, weightFunction, random);

      final Suspiciousness suspiciousness = suspiciousnessRoulette.exec();
      final Base base = makeBase(suspiciousness);
      final Gene gene = makeGene(variant.getGene(), base);
      final HistoricalElement element = new MutationHistoricalElement(variant, base);

      generatedVariants.add(variantStore.createVariant(gene, element));
    }

    return generatedVariants;
  }

  private Base makeBase(Suspiciousness suspiciousness) {
    final JDTASTLocation location = (JDTASTLocation) suspiciousness.getLocation();
    final JDTOperation operation = new MoveAfterOperation(chooseNodeAtRandom(location));

    return new Base(location, operation);
  }

  private ASTNode chooseNodeAtRandom(final JDTASTLocation location) {
    final GeneratedAST<?> generatedAST = location.getGeneratedAST();
    final FullyQualifiedName fqn = generatedAST.getPrimaryClassName();
    final Statement statement = (Statement) location.node;
    final MethodName methodName = new MethodName(fqn, statement);
    final Scope scope = new Scope(type, methodName);

    return candidateSelection.exec(scope);
  }

  private Gene makeGene(final Gene parent, final Base base) {
    final List<Base> bases = new ArrayList<>(parent.getBases());
    bases.add(base);
    return new Gene(bases);
  }
}
