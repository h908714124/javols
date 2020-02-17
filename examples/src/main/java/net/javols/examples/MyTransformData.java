package net.javols.examples;

import net.javols.Data;
import net.javols.Key;

import java.util.function.Function;

@Data(transform = MyTransformData.Tr.class)
abstract class MyTransformData {

  @Key(value = "mapper", mappedBy = Ma.class)
  abstract Integer mapper();

  @Key(value = "auto")
  abstract Integer auto();

  static class Ma implements Function<String, Integer> {
    @Override
    public Integer apply(String s) {
      return Math.max(Integer.parseInt(s), 0);
    }
  }

  static class Tr implements Function<Object, String> {
    @Override
    public String apply(Object o) {
      return o.toString();
    }
  }
}
