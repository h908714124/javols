package net.javols.compiler;

import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Collections.singletonList;
import static net.javols.compiler.ProcessorTest.fromSource;

class ValueTypeTest {

  @Test
  void objectType() {
    JavaFileObject javaFile = fromSource(
        "@Data(valueClass = Object.class)",
        "abstract class Arguments {",
        "",
        "  @Key(value = \"x\", mappedBy = ArrayMapper.class)",
        "  abstract Optional<int[]> foo();",
        "",
        "  static class ArrayMapper implements Function<String, int[]> {",
        "    public int[] apply(String s) {",
        "      return null;",
        "    }",
        "  }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Object");
  }
}
