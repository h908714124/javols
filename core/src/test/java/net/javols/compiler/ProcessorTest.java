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
  void emptyName() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
        "  @Key(\"\") abstract String a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("The name may not be empty");
  }

  @Test
  void duplicateName() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
        "  @Key(\"x\") abstract String a();",
        "  @Key(\"x\") abstract String b();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Duplicate long name");
  }

  @Test
  void unknownReturnType() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
        "",
        "  @Key(\"x\")",
        "  abstract StringBuilder a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Unknown key type: java.lang.StringBuilder. Try defining a custom mapper.");
  }

  @Test
  void declaredException() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
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
        "@Data",
        "abstract class Arguments {",
        "",
        "  @Key(\"x\")",
        "  String a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("method must be abstract");
  }

  @Test
  void rawOptional() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
        "",
        "  @Key(\"x\")",
        "  abstract Optional a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Unknown key type: java.util.Optional. Try defining a custom mapper.");
  }

  @Test
  void parameterizedSet() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
        "",
        "  @Key(\"x\")",
        "  abstract java.util.Set<String> a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Unknown key type: java.util.Set<java.lang.String>. Try defining a custom mapper.");
  }

  @Test
  void integerArray() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
        "",
        "  @Key(\"x\")",
        "  abstract int[] a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Unknown key type: int[]. Try defining a custom mapper.");
  }

  @Test
  void utilDate() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
        "",
        "  @Key(\"x\")",
        "  abstract java.util.Date a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Unknown parameter type: java.util.Date. Try defining a custom mapper.");
  }

  @Test
  void interfaceNotClass() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "interface Arguments {",
        "  abstract String a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Use a class, not an interface");
  }

  @Test
  void whitespaceInName() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
        "  @Key(\"a \")",
        "  abstract String a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("may not contain whitespace");
  }

  @Test
  void noMethods() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("Define at least one abstract method");
  }

  @Test
  void oneOptionalIntNotOptional() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
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
        "@Data",
        "abstract class Arguments {",
        "",
        "  @Key(\"x\")",
        "  abstract OptionalInt b();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void simpleFlag() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
        "",
        "  @Key(\"x\")",
        "  abstract boolean x();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .compilesWithoutError();
  }

  @Test
  void simpleInt() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
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
        "abstract class Arguments {",
        "",
        "  @Data",
        "  static abstract class Foo extends Arguments {",
        "    abstract String a();",
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
        "interface Arguments {",
        "",
        "  @Data",
        "  abstract class Foo implements Arguments {",
        "    abstract String a();",
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
        "abstract class Arguments {",
        "  @Key(\"a\") abstract String a();",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("The class must have the @Command annotation");
  }

  @Test
  void abstractMethodHasParameter() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
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
        "@Data",
        "abstract class Arguments {",
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
        "@Data",
        "abstract class Arguments {",
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
        "@Data",
        "abstract class Arguments {",
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
  void privateEnum() {
    JavaFileObject javaFile = fromSource(
        "@Data",
        "abstract class Arguments {",
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
        .withErrorContaining("Unknown parameter type: test.Arguments.Foo. Try defining a custom mapper.");
  }


  @Test
  void invalidNesting() {
    JavaFileObject javaFile = fromSource(
        "class Bob {",
        "  private static class Foo {",
        "    @Data",
        "    abstract static class Bar {",
        "    }",
        "  }",
        "}");
    assertAbout(javaSources()).that(singletonList(javaFile))
        .processedWith(new Processor())
        .failsToCompile()
        .withErrorContaining("The class may not not be private");
  }

  static JavaFileObject fromSource(String... lines) {
    List<String> sourceLines = withImports(lines);
    return forSourceLines("test.Arguments", sourceLines);
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
        "import java.util.function.BiConsumer;",
        "import java.util.function.BinaryOperator;",
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
