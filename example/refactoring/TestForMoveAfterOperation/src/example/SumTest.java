package example;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SumTest {

  @Test
  public void test01() {
    assertEquals(6, new Sum().sum());
  }

}
