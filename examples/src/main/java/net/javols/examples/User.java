package net.javols.examples;

import net.javols.Data;
import net.javols.Key;

import java.util.function.Function;

@Data
abstract class User {

  @Key("name")
  abstract String name();

  @Key(value = "age", mappedBy = NumberMapper.class)
  abstract int age();

  static class NumberMapper implements Function<String, Integer> {
    public Integer apply(String s) {
      int result = Integer.parseInt(s);
      if (result < 0) throw new IllegalArgumentException("Invalid: " + s);
      return result;
    }
  }
}