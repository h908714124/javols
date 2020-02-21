package net.javols.examples;

import net.javols.Data;
import net.javols.Key;

import java.net.ProxySelector;
import java.util.Optional;
import java.util.OptionalInt;

@Data(String.class)
abstract class MyData {

  private final int myLuckyNumber;

  MyData(int luckyNumber) {
    this.myLuckyNumber = luckyNumber;
  }

  @Key("apiKey")
  abstract String apiKey();

  @Key("secret")
  abstract OptionalInt secret();

  @Key("proxy")
  abstract Optional<ProxySelector> proxy();

  public int getMyLuckyNumber() {
    return myLuckyNumber;
  }
}
