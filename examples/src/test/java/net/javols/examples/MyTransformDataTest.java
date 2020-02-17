package net.javols.examples;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyTransformDataTest {

  @Test
  void testMyData() {
    Map<String, String> m = new HashMap<>();
    m.put("auto", "-1");
    m.put("mapper", "-1");
    m.put("proxy", "proxy.intra.net:1234");
    MyTransformData data = MyTransformData_Parser.parse(m::get);
    assertEquals(-1, data.auto());
    assertEquals(0, data.mapper());
  }
}