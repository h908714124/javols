package net.javols.examples;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyTransformDataTest {

  @Test
  void testMyData() {
    Map<String, BigInteger> m = new HashMap<>();
    m.put("mapper", BigInteger.ONE.negate());
    MyTransformData data = MyTransformData_Parser.parse(m::get);
    assertEquals(-1, data.mapper());
  }
}