package net.javol.coerce;

import com.squareup.javapoet.CodeBlock;
import net.javol.coerce.either.Either;
import net.javol.coerce.either.Right;
import net.javol.compiler.EvaluatingProcessor;
import net.javol.compiler.TypeExpr;
import net.javol.compiler.TypeTool;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapperClassValidatorTest {

  @Test
  void simpleTest() {

    EvaluatingProcessor.source(
        "import java.util.List;",
        "import java.util.function.Supplier;",
        "import java.util.function.Function;",
        "",
        "class Mapper<AA1, AA2> implements A<AA1, AA2> { public Function<AA1, List<AA2>> get() { return null; } }",
        "interface A<BB1, BB2> extends B<BB1, List<BB2>> { }",
        "interface B<CC1, CC2> extends C<CC1, CC2> { }",
        "interface C<DD1, DD2> extends Supplier<Function<DD1, DD2>> { }"
    ).run("Mapper", (elements, types) -> {
      TypeElement mapperClass = elements.getTypeElement("Mapper");
      TypeTool tool = new TypeTool(elements, types);
      DeclaredType expectedReturnType = TypeExpr.prepare(elements, types).parse("java.util.List<java.lang.Integer>");
      Either<String, CodeBlock> either = new MapperClassValidator(s -> null, tool, expectedReturnType, mapperClass)
          .checkReturnType();
      assertTrue(either instanceof Right);
      CodeBlock mapExpr = ((Right<String, CodeBlock>) either).value();
      CodeBlock expected = CodeBlock.of("new $T<$T, $T>().get()", types.erasure(mapperClass.asType()), String.class, Integer.class);
      assertEquals(expected, mapExpr);
    });
  }
}
