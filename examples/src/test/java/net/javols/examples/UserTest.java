package net.javols.examples;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

  private User_Parser parser = User_Parser.create()
      .nameMapper(Function.identity())
      .ageMapper(new User.NumberMapper());

  @Test
  void age() {
    Map<String, String> m = Map.of("name", "Hauke", "age", "26");
    User user = parser.parse(m::get);
    assertEquals("Hauke", user.name());
    assertEquals(26, user.age());
  }
}