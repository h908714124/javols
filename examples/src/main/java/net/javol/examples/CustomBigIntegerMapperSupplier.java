package net.javol.examples;

import java.math.BigInteger;
import java.util.function.Function;
import java.util.function.Supplier;

class CustomBigIntegerMapperSupplier implements Supplier<Function<String, BigInteger>> {

  @Override
  public Function<String, BigInteger> get() {
    return s -> {
      if (s.startsWith("0x")) {
        return new BigInteger(s.substring(2), 16);
      }
      return new BigInteger(s);
    };
  }
}
