package example;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class UtilityTest {

  @Test
  public void testProduct01() {
    final List<Double> numbers = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
    final double actual = Utility.product(numbers);

    final double expected = 120.0;

    Assert.assertEquals(expected, actual, 0.001);
  }

  @Test
  public void testProduct02() {
    final List<Double> numbers = Arrays.asList(6.0, 7.0, 8.0, 9.0, 10.0);
    final double actual = Utility.product(numbers);

    final double expected = 30240.0;

    Assert.assertEquals(expected, actual, 0.001);
  }
}
