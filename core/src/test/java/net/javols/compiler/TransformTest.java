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
        "@Data(Integer.class)",
        "abstract class MyData {",
        "",
        "  @Key(value = \"x\")",
        "  abstract Optional<String> foo();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void abstractDataType() {
    JavaFileObject javaFile = fromSource(
        "@Data(MyData.Foo.class)",
        "abstract class MyData {",
        "",
        "  interface Foo {}",
        "",
        "  @Key(value = \"x\")",
        "  abstract Optional<Foo> foo();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }
}
