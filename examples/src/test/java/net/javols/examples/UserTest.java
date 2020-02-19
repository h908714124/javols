package net.javols.examples;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

  @Test
  void age() {
    Map<String, String> m = Map.of("name", "Hauke", "age", "26");
    User user = new User_Parser(Function.identity(), new User.NumberMapper()).parse(m::get);
    assertEquals("Hauke", user.name());
    assertEquals(26, user.age());
  }
}