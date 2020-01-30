package net.javols.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.Proxy;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MySupplierDataTest {

  @Test
  void testMyData() {
    Map<String, String> m = new HashMap<>();
    m.put("apiKey", "A");
    m.put("secret", "B");
    m.put("proxy", "proxy.intra.net:1234");
    MySupplierData data = MySupplierData_Parser.parse(m::get);
    Assertions.assertEquals("A", data.apiKey());
    Assertions.assertEquals("B", data.secret());
    Assertions.assertTrue(data.proxy().isPresent());
    List<Proxy> proxies = data.proxy().get().select(URI.create("http://foo.de"));
    Assertions.assertEquals(1, proxies.size());
    Assertions.assertEquals("HTTP @ proxy.intra.net:1234", proxies.get(0).toString());
  }

  @Test
  void testProxyAbsent() {
    Map<String, String> m = new HashMap<>();
    m.put("apiKey", "A");
    m.put("secret", "B");
    MySupplierData data = MySupplierData_Parser.parse(m::get);
    Assertions.assertEquals("A", data.apiKey());
    Assertions.assertEquals("B", data.secret());
    Assertions.assertFalse(data.proxy().isPresent());
  }

  @Test
  void testSecretAbsent() {
    Map<String, String> m = new HashMap<>();
    m.put("apiKey", "A");
    Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> MySupplierData_Parser.parse(m::get));
    Assertions.assertEquals("Missing required key: <secret>", e.getMessage());
  }
}