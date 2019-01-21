package example;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class FooTest {

  @Test
  public void test01() {
    assertEquals(15, new Foo().foo());
  }

}
