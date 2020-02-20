package net.javols.examples;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyTransformDataTest {

  private MyTransformData_Parser parser = MyTransformData_Parser.create()
      .numberMapper(new MyTransformData.Ma());

  @Test
  void testMyNumber() {
    Map<String, BigInteger> m = new HashMap<>();
    m.put("number", BigInteger.ONE.negate());
    Function<String, BigInteger> get = m::get;
    Function<String, BigDecimal> f = get.andThen(new MyTransformData.Tr());
    MyTransformData data = parser.parse(f);
    assertEquals(-1, data.number());
  }
}