package net.javol.examples;

import net.javol.Key;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

abstract class MyData {

  @Key("apiKey")
  abstract String apiKey();

  @Key("secret")
  abstract String secret();

  @Key(value = "proxy", mappedBy = ProxyMapper.class)
  abstract Optional<ProxySelector> proxy();

  static class ProxyMapper implements Function<String, ProxySelector> {

    @Override
    public ProxySelector apply(String proxyString) {
      String[] tokens = proxyString.split(":", 2);
      String host = tokens[0];
      int port = Integer.parseInt(tokens[1]);
      InetSocketAddress address = new InetSocketAddress(host, port);
      return new StaticProxySelector(address);
    }
  }

  private static class StaticProxySelector extends ProxySelector {
    List<Proxy> proxies;

    StaticProxySelector(InetSocketAddress address) {
      this.proxies = Collections.singletonList(new Proxy(Proxy.Type.HTTP, address));
    }

    public void connectFailed(URI uri, SocketAddress sa, IOException e) {
    }

    public synchronized List<Proxy> select(URI uri) {
      String scheme = uri.getScheme().toLowerCase();
      return !scheme.equals("http") && !scheme.equals("https") ?
          Collections.singletonList(Proxy.NO_PROXY) : proxies;
    }
  }
}
