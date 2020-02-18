package net.javols.examples;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyTransformDataTest {

  @Test
  void testMyNumber() {
    Map<String, BigInteger> m = new HashMap<>();
    m.put("number", BigInteger.ONE.negate());
    MyTransformData data = MyTransformData_Parser.parse(m::get);
    assertEquals(-1, data.number());
  }
}