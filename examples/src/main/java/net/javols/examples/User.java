package net.javols.examples;

import net.javols.Data;
import net.javols.Key;

@Data(String.class)
abstract class User {

  @Key("name")
  abstract String name();

  @Key(value = "age")
  abstract int age();
}
