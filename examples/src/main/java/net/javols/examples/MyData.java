package net.javols.examples;

import net.javols.Data;
import net.javols.Key;

import java.net.ProxySelector;
import java.util.Optional;

@Data(String.class)
abstract class MyData {

  @Key("apiKey")
  abstract String apiKey();

  @Key("secret")
  abstract String secret();

  @Key("proxy")
  abstract Optional<ProxySelector> proxy();
}
