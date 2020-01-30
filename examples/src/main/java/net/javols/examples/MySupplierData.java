package net.javols.examples;

import net.javols.Data;
import net.javols.Key;

import java.net.ProxySelector;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Data
abstract class MySupplierData {

  @Key("apiKey")
  abstract String apiKey();

  @Key("secret")
  abstract String secret();

  @Key(value = "proxy", mappedBy = ProxyMapperSupplier.class)
  abstract Optional<ProxySelector> proxy();

  static class ProxyMapperSupplier implements Supplier<Function<String, ProxySelector>> {
    public Function<String, ProxySelector> get() {
      return new ProxyMapper();
    }
  }
}
