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
        "@Data(valueType = Integer.class)",
        "abstract class MyData {",
        "",
        "  @Key(value = \"x\")",
        "  abstract Optional<String> foo();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }
}
