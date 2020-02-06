package net.javols.examples;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

  @Test
  void age() {
    Map<String, String> m = Map.of("name", "Hauke", "age", "26");
    User user = User_Parser.parse(m::get);
    assertEquals("Hauke", user.name());
    assertEquals(26, user.age());
  }
}