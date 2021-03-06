package net.javols.examples;

import net.javols.Data;
import net.javols.Key;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

@Data(BigDecimal.class)
abstract class MyNonStringData {

  @Key("number")
  abstract Integer number();

  static class Ma implements Function<BigDecimal, Integer> {
    @Override
    public Integer apply(BigDecimal s) {
      return s.intValue();
    }
  }

  static class Tr implements Function<BigInteger, BigDecimal> {
    @Override
    public BigDecimal apply(BigInteger bigInteger) {
      return new BigDecimal(bigInteger);
    }
  }
}
