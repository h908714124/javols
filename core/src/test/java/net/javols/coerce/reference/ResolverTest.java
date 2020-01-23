package net.javols.coerce.reference;

import net.javols.coerce.either.Either;
import net.javols.coerce.either.Left;
import net.javols.coerce.either.Right;
import net.javols.compiler.EvaluatingProcessor;
import net.javols.compiler.TypeTool;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.javols.coerce.reference.ExpectedType.FUNCTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResolverTest {

  @Test
  void testTypecheckSuccess() {

    EvaluatingProcessor.source(
        "package test;",
        "",
        "import java.util.function.Supplier;",
        "",
        "interface StringSupplier extends Supplier<String> { }",
        "",
        "abstract class Foo implements StringSupplier { }"
    ).run("Mapper", (elements, types) -> {
      TypeTool tool = new TypeTool(elements, types);
      TypeElement mapper = elements.getTypeElement("test.Foo");
      Resolver resolver = new Resolver(FUNCTION, tool);
      Either<TypecheckFailure, List<? extends TypeMirror>> result = resolver.typecheck(mapper, Supplier.class);
      assertTrue(result instanceof Right);
      List<? extends TypeMirror> typeArguments = ((Right<TypecheckFailure, List<? extends TypeMirror>>) result).value();
      assertEquals(1, typeArguments.size());
      TypeMirror typeParameter = typeArguments.get(0);
      TypeElement string = elements.getTypeElement("java.lang.String");
      assertTrue(types.isSameType(string.asType(), typeParameter));
    });
  }

  @Test
  void testTypecheckFail() {

    EvaluatingProcessor.source(
        "package test;",
        "",
        "import java.util.function.Supplier;",
        "",
        "interface StringSupplier extends Supplier<String> { }",
        "",
        "abstract class Foo implements StringSupplier { }"
    ).run("Mapper", (elements, types) -> {
      TypeTool tool = new TypeTool(elements, types);
      TypeElement mapper = elements.getTypeElement("test.Foo");
      Either<TypecheckFailure, List<? extends TypeMirror>> result = new Resolver(FUNCTION, tool).typecheck(mapper, String.class);
      assertTrue(result instanceof Left);
    });
  }

  @Test
  void testTypecheckFunction() {

    EvaluatingProcessor.source(
        "package test;",
        "",
        "import java.util.function.Supplier;",
        "import java.util.function.Function;",
        "",
        "interface FunctionSupplier extends Supplier<Function<String, String>> { }"
    ).run("Mapper", (elements, types) -> {
      TypeTool tool = new TypeTool(elements, types);
      TypeElement mapper = elements.getTypeElement("test.FunctionSupplier");
      DeclaredType declaredType = TypeTool.asDeclared(mapper.getInterfaces().get(0));
      DeclaredType functionType = TypeTool.asDeclared(declaredType.getTypeArguments().get(0));
      Either<TypecheckFailure, List<? extends TypeMirror>> result = new Resolver(FUNCTION, tool).typecheck(functionType, Function.class);
      assertTrue(result instanceof Right);
    });
  }
}
