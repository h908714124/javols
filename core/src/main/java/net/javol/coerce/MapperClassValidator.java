package net.javol.coerce;

import com.squareup.javapoet.CodeBlock;
import net.javol.coerce.either.Either;
import net.javol.coerce.reference.ReferenceTool;
import net.javol.coerce.reference.ReferencedType;
import net.javol.compiler.TypeTool;
import net.javol.compiler.TypevarMapping;
import net.javol.compiler.ValidationException;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.function.Function;

import static net.javol.coerce.SuppliedClassValidator.commonChecks;
import static net.javol.coerce.Util.checkNotAbstract;
import static net.javol.coerce.Util.getTypeParameterList;
import static net.javol.coerce.reference.ExpectedType.FUNCTION;

public final class MapperClassValidator {

  private final Function<String, ValidationException> errorHandler;
  private final TypeTool tool;
  private final TypeMirror expectedReturnType;
  private final TypeElement mapperClass;

  public MapperClassValidator(Function<String, ValidationException> errorHandler, TypeTool tool, TypeMirror expectedReturnType, TypeElement mapperClass) {
    this.errorHandler = errorHandler;
    this.tool = tool;
    this.expectedReturnType = expectedReturnType;
    this.mapperClass = mapperClass;
  }

  public Either<String, CodeBlock> checkReturnType() {
    commonChecks(mapperClass);
    checkNotAbstract(mapperClass);
    ReferencedType<Function> functionType = new ReferenceTool<>(FUNCTION, errorHandler, tool, mapperClass).getReferencedType();
    TypeMirror inputType = functionType.typeArguments().get(0);
    TypeMirror outputType = functionType.typeArguments().get(1);
    return tool.unify(tool.asType(String.class), inputType).flatMap(FUNCTION::boom, leftSolution ->
        handle(functionType, outputType, leftSolution));
  }

  private Either<String, CodeBlock> handle(ReferencedType<Function> functionType, TypeMirror outputType, TypevarMapping leftSolution) {
    return tool.unify(expectedReturnType, outputType).flatMap(FUNCTION::boom, rightSolution ->
        handle(functionType, leftSolution, rightSolution));
  }

  private Either<String, CodeBlock> handle(ReferencedType<Function> functionType, TypevarMapping leftSolution, TypevarMapping rightSolution) {
    return new Flattener(tool, mapperClass)
        .mergeSolutions(leftSolution, rightSolution)
        .map(FUNCTION::boom, typeParameters -> CodeBlock.of("new $T$L()$L",
            tool.erasure(mapperClass.asType()),
            getTypeParameterList(typeParameters.getTypeParameters()),
            functionType.isSupplier() ? ".get()" : ""));
  }
}
