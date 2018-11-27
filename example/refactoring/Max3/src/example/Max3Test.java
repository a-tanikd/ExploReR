package example;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class Max3Test {
  @Test
  public void test01() {
    assertEquals(3, new Max3().max3(1, 2, 3));
  }

  @Test
  public void test02() {
    assertEquals(3, new Max3().max3(1, 3, 2));
  }

  @Test
  public void test03() {
    assertEquals(3, new Max3().max3(2, 1, 3));
  }

  @Test
  public void test04() {
    assertEquals(3, new Max3().max3(2, 3, 1));
  }

  @Test
  public void test05() {
    assertEquals(3, new Max3().max3(3, 1, 2));
  }

  @Test
  public void test06() {
    assertEquals(3, new Max3().max3(3, 2, 1));
  }
}
