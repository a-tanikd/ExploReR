package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import org.eclipse.jdt.core.dom.Statement;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import jp.kusumotolab.kgenprog.ga.Roulette;
import jp.kusumotolab.kgenprog.ga.mutation.MethodName;
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public abstract class StatementSelection implements CandidateSelection {

  private final Random random;
  private Roulette<ReuseCandidate<Statement>> projectLevelCandidateRoulette;
  private final Multimap<String, ReuseCandidate<Statement>> packageToCandidates = ArrayListMultimap.create();
  private final Multimap<FullyQualifiedName, ReuseCandidate<Statement>> fileToCandidates = ArrayListMultimap.create();
  private final Multimap<MethodName, ReuseCandidate<Statement>> methodToCandidates = ArrayListMultimap.create();
  private final Map<String, Roulette<ReuseCandidate<Statement>>> packageLevelCandidateRouletteCache = new HashMap<>();
  private final Map<FullyQualifiedName, Roulette<ReuseCandidate<Statement>>> fileLevelCandidateRouletteCache = new HashMap<>();
  private final Map<MethodName, Roulette<ReuseCandidate<Statement>>> methodLevelCandidateRouletteCache = new HashMap<>();

  public StatementSelection(final Random random) {
    this.random = random;
  }

  @Override
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    final StatementVisitor visitor = new StatementVisitor(candidates);
    final List<ReuseCandidate<Statement>> reuseCandidates = visitor.getReuseCandidateList();

    clearAndSetCandidates(reuseCandidates);

    projectLevelCandidateRoulette = createRoulette(reuseCandidates);
  }

  public abstract double getStatementWeight(final ReuseCandidate<Statement> reuseCandidate);

  @Override
  public Statement exec(final Scope scope) {
    final Roulette<ReuseCandidate<Statement>> roulette = getRoulette(scope);
    final ReuseCandidate<Statement> candidate = roulette.exec();
    return candidate.getValue();
  }

  private void clearAndSetCandidates(final List<ReuseCandidate<Statement>> reuseCandidates) {
    packageToCandidates.clear();
    fileToCandidates.clear();
    methodToCandidates.clear();

    packageLevelCandidateRouletteCache.clear();
    fileLevelCandidateRouletteCache.clear();
    methodLevelCandidateRouletteCache.clear();

    for (final ReuseCandidate<Statement> reuseCandidate : reuseCandidates) {
      packageToCandidates.put(reuseCandidate.getPackageName(), reuseCandidate);
      fileToCandidates.put(reuseCandidate.getFqn(), reuseCandidate);

      final MethodName methodName = new MethodName(reuseCandidate);
      if (!Strings.isNullOrEmpty(methodName.getMethodName())) {
        methodToCandidates.put(methodName, reuseCandidate);
      }
    }
  }

  private Roulette<ReuseCandidate<Statement>> getRouletteInProjectScope() {
    return projectLevelCandidateRoulette;
  }

  private Roulette<ReuseCandidate<Statement>> getRouletteInPackage(final String packageName) {
    return getRoulette(packageName, packageLevelCandidateRouletteCache,
        packageToCandidates);
  }

  private Roulette<ReuseCandidate<Statement>> getRouletteInFile(final FullyQualifiedName fqn) {
    return getRoulette(fqn, fileLevelCandidateRouletteCache, fileToCandidates);
  }

  private Roulette<ReuseCandidate<Statement>> getRouletteInMethod(final MethodName methodName) {
    return getRoulette(methodName, methodLevelCandidateRouletteCache, methodToCandidates);
  }

  /**
   * 指定された key に対応する CandidateRoulette を返す
   */
  private <T> Roulette<ReuseCandidate<Statement>> getRoulette(final T key,
      final Map<T, Roulette<ReuseCandidate<Statement>>> rouletteCache,
      final Multimap<T, ReuseCandidate<Statement>> candidateMap) {
    Roulette<ReuseCandidate<Statement>> roulette = rouletteCache.get(key);
    if (roulette != null) {
      return roulette;
    }
    final Collection<ReuseCandidate<Statement>> candidates = candidateMap.get(key);
    roulette = createRoulette(new ArrayList<>(candidates));
    rouletteCache.put(key, roulette);
    return roulette;
  }

  public Roulette<ReuseCandidate<Statement>> getRoulette(final Scope scope) {
    final FullyQualifiedName fqn = scope.getFqn();
    switch (scope.getType()) {
      case PROJECT:
        return getRouletteInProjectScope();
      case PACKAGE:
        return getRouletteInPackage(fqn.getPackageName());
      case FILE:
        return getRouletteInFile(fqn);
      case METHOD:
        return getRouletteInMethod(scope.getMethodName());
    }
    throw new IllegalArgumentException("This scope is not implemented.");
  }

  private Roulette<ReuseCandidate<Statement>> createRoulette(
      final List<ReuseCandidate<Statement>> candidates) {
    final Function<ReuseCandidate<Statement>, Double> weightFunction = this::getStatementWeight;
    return new Roulette<>(candidates, weightFunction, random);
  }
}
