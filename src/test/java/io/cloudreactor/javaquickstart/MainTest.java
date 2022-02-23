package io.cloudreactor.javaquickstart;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** JUnit5 example test adopted from
 *
 *  https://github.com/junit-team/junit5-samples/blob/r5.8.2/junit5-jupiter-starter-gradle/src/test/java/com/example/project/CalculatorTests.java
 *
 */
public class MainTest {
  @Test
  @DisplayName("1 + 1 = 2")
  void addsTwoNumbers() {
    assertEquals(2, Main.add(1, 1), "1 + 1 should equal 2");
  }

  @ParameterizedTest(name = "{0} + {1} = {2}")
  @CsvSource({
      "0,    1,   1",
      "1,    2,   3",
      "49,  51, 100",
      "1,  100, 101"
  })
  void add(int first, int second, int expectedResult) {
    assertEquals(expectedResult, Main.add(first, second),
      () -> first + " + " + second + " should equal " + expectedResult);
  }
}
