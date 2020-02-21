package net.javols.examples;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

  private User_Parser parser = User_Parser.create()
      .nameMapper(Function.identity())
      .ageMapper(new NumberMapper());

  @Test
  void age() {
    Map<String, String> m = Map.of("name", "Hauke", "age", "26");
    User user = parser.prepare().parse(m::get);
    assertEquals("Hauke", user.name());
    assertEquals(26, user.age());
  }


  private static class NumberMapper implements Function<String, Integer> {
    public Integer apply(String s) {
      int result = Integer.parseInt(s);
      if (result < 0) throw new IllegalArgumentException("Invalid: " + s);
      return result;
    }
  }
}