package net.javols.coerce;

public enum NonFlagSkew {

  REPEATABLE, OPTIONAL, REQUIRED;

  Skew widen() {
    return Skew.valueOf(name());
  }
}
