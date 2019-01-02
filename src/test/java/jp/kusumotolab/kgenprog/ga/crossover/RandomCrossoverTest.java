package jp.kusumotolab.kgenprog.ga.crossover;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class RandomCrossoverTest {

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントもランダムで選択するランダム交叉のテスト
   */
  @Test
  public void test01() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0)
        .thenReturn(1)
        .thenReturn(2);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantRandomSelection(random), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantA，2つ目のバリアントはvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 常に2番目のBaseを取得するはず．つまり，すべてnoneBaseになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントもランダムで選択するランダム交叉のテスト
   */
  @Test
  public void test02() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(1)
        .thenReturn(2)
        .thenReturn(3);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantRandomSelection(random), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantB，2つ目のバリアントはvariantCが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantB, testVariants.variantC);

    // 常に3番目のBaseを取得するはず．つまり，insert, none, insert, insertになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.insertBase, testVariants.noneBase,
        testVariants.insertBase, testVariants.insertBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントを遺伝子の類似度で選択するランダム交叉のテスト
   */
  @Test
  public void test03() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantGeneSimilarityBasedSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantA，2つ目のバリアントはvariantDが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantD);

    // 常に0番目のBaseを取得するはず．つまり，すべてnoneBaseになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントを遺伝子の類似度で選択するランダム交叉のテスト
   */
  @Test
  public void test04() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(2);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantGeneSimilarityBasedSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantC，2つ目のバリアントはvariantAが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantC, testVariants.variantA);

    // 常に3番目のBaseを取得するはず．つまり，insert, insert, none, noneになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.insertBase, testVariants.insertBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントをテスト結果の類似度で選択するランダム交叉のテスト
   */
  @Test
  public void test05() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(0);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantTestSimilarityBasedSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantA，2つ目のバリアントはvariantBが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantA, testVariants.variantB);

    // 常に0番目のBaseを取得するはず．つまり，すべてnoneBaseになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.noneBase, testVariants.noneBase,
        testVariants.noneBase, testVariants.noneBase);
  }

  /**
   * 一つ目のバリアントをランダム，二つ目のバリアントをテスト結果の類似度で選択するランダム交叉のテスト
   */
  @Test
  public void test06() {

    // 生成するバリアントを制御するための疑似乱数
    final Random random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(true);
    when(random.nextInt(anyInt())).thenReturn(2);

    // バリアントの生成
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantTestSimilarityBasedSelection(), 1);
    final CrossoverTestVariants testVariants = new CrossoverTestVariants();
    final List<Variant> variants = crossover.exec(testVariants.variantStore);
    final Variant variant = variants.get(0);

    // 1つ目のバリアントはvariantC，2つ目のバリアントはvariantDが選ばれているはず
    final HistoricalElement element = variant.getHistoricalElement();
    assertThat(element.getParents()).containsExactly(testVariants.variantC, testVariants.variantD);

    // 常に3番目のBaseを取得するはず．つまり，insert, insert, insert, insertになっているはず．
    final Gene gene = variant.getGene();
    assertThat(gene.getBases()).containsExactly(testVariants.insertBase, testVariants.insertBase,
        testVariants.insertBase, testVariants.insertBase);
  }
}
