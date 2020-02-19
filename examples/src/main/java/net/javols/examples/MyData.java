package net.javols.examples;

import net.javols.Data;
import net.javols.Key;

import java.net.ProxySelector;
import java.util.Optional;

@Data
abstract class MyData {

  @Key("apiKey")
  abstract String apiKey();

  @Key("secret")
  abstract String secret();

  @Key(value = "proxy")
  abstract Optional<ProxySelector> proxy();
}
