package net.javols.examples;

import net.javols.Data;
import net.javols.Key;

import java.math.BigInteger;
import java.util.function.Function;
import java.util.function.Supplier;

@Data(transform = MyTransformData.Tr.class)
abstract class MyTransformData {

  @Key(value = "mapper", mappedBy = Ma.class)
  abstract Integer mapper();

  static class Ma implements Function<BigInteger, Integer> {
    @Override
    public Integer apply(BigInteger s) {
      return s.intValue();
    }
  }

  static class Tr implements Supplier<Function<BigInteger, BigInteger>> {
    @Override
    public Function<BigInteger, BigInteger> get() {
      return Function.identity();
    }
  }
}
