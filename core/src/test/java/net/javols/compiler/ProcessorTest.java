package net.javols.compiler;

import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Collections.singletonList;

class ProcessorTest {

  @Test
  void duplicateName() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "  @Key(\"x\") abstract String a();",
        "  @Key(\"x\") abstract String b();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Duplicate key: x");
  }

  @Test
  void unknownReturnType() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract StringBuilder a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void declaredException() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract String a() throws IllegalArgumentException;",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("may not declare any exceptions");
  }

  @Test
  void classNotAbstract() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  String a() { return null; }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("The method must be abstract");
  }

  @Test
  void rawOptional() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract Optional a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void parameterizedSet() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract java.util.Set<String> a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void integerArray() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract int[] a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void utilDate() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract java.util.Date a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void interfaceNotClass() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "interface Arguments {",
        "  abstract String a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Use a class, not an interface");
  }

  @Test
  void noMethods() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Define at least one abstract method");
  }

  @Test
  void oneOptionalIntNotOptional() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract OptionalInt b();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void oneOptionalInt() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract OptionalInt b();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void simpleInt() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract int aRequiredInt();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void extendsNotAllowed() {
    JavaFileObject javaFile = fromSource(
        "abstract class MyData {",
        "",
        "  @Data(String.class)",
        "  static abstract class Foo extends MyData {",
        "    @Key(\"x\") abstract String a();",
        "  }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("The model class may not implement or extend anything");
  }

  @Test
  void implementsNotAllowed() {
    JavaFileObject javaFile = fromSource(
        "interface MyData {",
        "",
        "  @Data(String.class)",
        "  abstract class Foo implements MyData {",
        "    @Key(\"x\") abstract String a();",
        "  }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("implement");
  }

  @Test
  void missingDataAnnotation() {
    JavaFileObject javaFile = fromSource(
        "abstract class MyData {",
        "  @Key(\"a\") abstract String a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("The class must have the @Data annotation");
  }

  @Test
  void abstractMethodHasParameter() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "  @Key(\"x\") abstract String a(int b, int c);",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("The method may not have parameters.");
  }

  @Test
  void typeParameter() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "  @Key(\"x\") abstract <E> String a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("The method may not have type parameters.");
  }

  @Test
  void missingAnnotation() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  abstract List<String> a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("missing @Key annotation");
  }

  @Test
  void innerEnum() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract Foo foo();",
        "",
        "  enum Foo {",
        "    BAR",
        "   }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void privateKey() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract Foo foo();",
        "",
        "  private enum Foo {",
        "    BAR",
        "   }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Unreachable key type.");
  }

  @Test
  void privateListKey() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "",
        "  @Key(\"x\")",
        "  abstract List<Foo> foo();",
        "",
        "  private enum Foo {",
        "    BAR",
        "   }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Unreachable key type.");
  }

  @Test
  void invalidNesting() {
    JavaFileObject javaFile = fromSource(
        "class MyData {",
        "  private static class Foo {",
        "    @Data(String.class)",
        "    abstract static class Bar {",
        "    }",
        "  }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("The class may not not be private");
  }

  @Test
  void constructor() {
    JavaFileObject javaFile = fromSource(
        "@Data(String.class)",
        "abstract class MyData {",
        "  private final Integer input;",
        "  MyData(Integer input) {",
        "    this.input = input;",
        "  }",
        "  @Key(\"x\") abstract String a();",
        "  Integer getInput() {",
        "    return input;",
        "  }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  static JavaFileObject fromSource(String... lines) {
    List<String> sourceLines = withImports(lines);
    return forSourceLines("test.MyData", sourceLines);
  }

  static List<String> withImports(String... lines) {
    List<String> header = Arrays.asList(
        "package test;",
        "",
        "import java.math.BigInteger;",
        "import java.util.List;",
        "import java.util.Set;",
        "import java.util.Map;",
        "import java.util.AbstractMap;",
        "import java.util.Collection;",
        "import java.util.Collections;",
        "import java.util.Optional;",
        "import java.util.OptionalInt;",
        "import java.util.function.Function;",
        "import java.util.function.Supplier;",
        "import java.util.stream.Collector;",
        "import java.util.stream.Collectors;",
        "import java.time.LocalDate;",
        "",
        "import net.javols.Data;",
        "import net.javols.Key;",
        "");
    List<String> moreLines = new ArrayList<>(lines.length + header.size());
    moreLines.addAll(header);
    Collections.addAll(moreLines, lines);
    return moreLines;
  }
}
