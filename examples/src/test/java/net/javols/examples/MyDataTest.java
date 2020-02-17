package net.javols.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.Proxy;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyDataTest {

  @Test
  void testMyData() {
    Map<String, String> m = new HashMap<>();
    m.put("apiKey", "A");
    m.put("secret", "B");
    m.put("proxy", "proxy.intra.net:1234");
    MyData data = MyData_Parser.parse(m::get);
    assertEquals("A", data.apiKey());
    assertEquals("B", data.secret());
    assertTrue(data.proxy().isPresent());
    List<Proxy> proxies = data.proxy().get().select(URI.create("http://foo.de"));
    assertEquals(1, proxies.size());
    assertEquals("HTTP @ proxy.intra.net:1234", proxies.get(0).toString());
  }

  @Test
  void testProxyAbsent() {
    Map<String, String> m = new HashMap<>();
    m.put("apiKey", "A");
    m.put("secret", "B");
    MyData data = MyData_Parser.parse(m::get);
    assertEquals("A", data.apiKey());
    assertEquals("B", data.secret());
    assertFalse(data.proxy().isPresent());
  }

  @Test
  void testSecretAbsent() {
    Map<String, String> m = new HashMap<>();
    m.put("apiKey", "A");
    Exception e = Assertions.<IllegalArgumentException>assertThrows(IllegalArgumentException.class, () -> MyData_Parser.parse(m::get));
    assertEquals("Missing required key: <secret>", e.getMessage());
  }
}