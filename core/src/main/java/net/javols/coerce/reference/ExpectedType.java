package net.javols.coerce.reference;

import java.util.function.Function;

public class ExpectedType {

  public static String boom(String message) {
    return String.format("There is a problem with the mapper class: %s.", message);
  }

  static String simpleName() {
    return Function.class.getSimpleName();
  }
}
