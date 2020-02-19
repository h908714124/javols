package net.javols.examples;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyTransformDataTest {

  @Test
  void testMyNumber() {
    Map<String, BigInteger> m = new HashMap<>();
    m.put("number", BigInteger.ONE.negate());
    Function<String, BigInteger> get = m::get;
    Function<String, BigDecimal> f = get.andThen(new MyTransformData.Tr());
    MyTransformData data = new MyTransformData_Parser(new MyTransformData.Ma())
        .parse(f);
    assertEquals(-1, data.number());
  }
}