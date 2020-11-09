package net.javols.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.Proxy;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyDataTest {

  private MyData_Parser parser = MyData_Parser.create()
      .apiKeyMapper(Function.identity())
      .secretMapper(Integer::parseInt)
      .proxyMapper(new ProxyMapper());

  @Test
  void testMyData() {
    Map<String, String> m = new HashMap<>();
    m.put("apiKey", "A");
    m.put("secret", "1");
    m.put("proxy", "proxy.intra.net:1234");
    MyData data = parser.prepare(0).parse(m::get);
    assertEquals("A", data.apiKey());
    assertEquals(OptionalInt.of(1), data.secret());
    assertTrue(data.proxy().isPresent());
    List<Proxy> proxies = data.proxy().get().select(URI.create("http://foo.de"));
    assertEquals(1, proxies.size());
    assertEquals("HTTP @ proxy.intra.net/<unresolved>:1234", proxies.get(0).toString());
  }

  @Test
  void testProxyAbsent() {
    Map<String, String> m = new HashMap<>();
    m.put("apiKey", "A");
    m.put("secret", "1");
    MyData data = parser.prepare(13).parse(m::get);
    assertEquals("A", data.apiKey());
    assertEquals(13, data.getMyLuckyNumber());
    assertEquals(OptionalInt.of(1), data.secret());
    assertFalse(data.proxy().isPresent());
  }

  @Test
  void testApiKeyAbsent() {
    Map<String, String> m = new HashMap<>();
    m.put("secret", "1");
    Exception e = Assertions.assertThrows(IllegalArgumentException.class,
        () -> parser.prepare(0).parse(m::get));
    assertEquals("Missing required key: <apiKey>", e.getMessage());
  }
}