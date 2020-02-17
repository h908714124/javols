package net.javols.compiler;

import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Collections.singletonList;
import static net.javols.compiler.ProcessorTest.fromSource;

class TransformTest {

  @Test
  void missingMapper() {
    JavaFileObject javaFile = fromSource(
        "@Data(transform = MyData.Tr.class)",
        "abstract class MyData {",
        "",
        "  @Key(value = \"x\")",
        "  abstract Optional<String> foo();",
        "",
        "  static class Tr implements Function<String, Integer> {",
        "    public Integer apply(String s) { return null; }",
        "  }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Define a custom mapper for this key.");
  }

  @Test
  void incompatibleMapper() {
    JavaFileObject javaFile = fromSource(
        "@Data(transform = MyData.Tr.class)",
        "abstract class MyData {",
        "",
        "  @Key(value = \"x\", mappedBy = Ma.class)",
        "  abstract Optional<int[]> foo();",
        "",
        "  static class Ma implements Function<String, int[]> {",
        "    public int[] apply(String s) { return null; }",
        "  }",
        "",
        "  static class Tr implements Function<String, Integer> {",
        "    public Integer apply(String s) { return null; }",
        "  }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Integer");
  }
}
