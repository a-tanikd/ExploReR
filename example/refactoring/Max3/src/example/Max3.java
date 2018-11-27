package example;

public class Max3 {

  public int max3(int a, int b, int c) {
    int max;

    if (a > b) {
      if (a > c) {
        max = a; // 3
      } else {
        max = c; // 3
      }
    } else {
      if (b > c) {
        max = b; // 3
      } else {
        max = c; // 3
      }
    }

    return max; // 2
  }
}
