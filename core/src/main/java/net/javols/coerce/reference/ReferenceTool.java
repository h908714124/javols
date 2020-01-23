package net.javols.coerce.reference;

import net.javols.compiler.TypeTool;
import net.javols.compiler.ValidationException;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.javols.compiler.TypeTool.asDeclared;

public class ReferenceTool<E> {

  private final Resolver resolver;

  private final Function<String, ValidationException> errorHandler;
  private final TypeElement referencedClass;
  private final ExpectedType<E> expectedType;

  public ReferenceTool(ExpectedType<E> expectedType, Function<String, ValidationException> errorHandler, TypeTool tool, TypeElement referencedClass) {
    this.expectedType = expectedType;
    this.errorHandler = errorHandler;
    this.referencedClass = referencedClass;
    this.resolver = new Resolver(expectedType, tool);
  }

  public ReferencedType<E> getReferencedType() {
    return resolver.typecheck(referencedClass, Supplier.class)
        .fold(this::handleNotSupplier, this::handleSupplier);
  }

  private ReferencedType<E> handleNotSupplier(TypecheckFailure failure) {
    if (failure.isFatal()) {
      throw boom(failure.getMessage());
    }
    List<? extends TypeMirror> expected = resolver.typecheck(referencedClass, expectedType.expectedClass())
        .orElseThrow(f -> boom(f.getMessage()));
    return new ReferencedType<>(expected, false);
  }

  private ReferencedType<E> handleSupplier(List<? extends TypeMirror> typeArguments) {
    TypeMirror supplied = typeArguments.get(0);
    if (supplied.getKind() != TypeKind.DECLARED) {
      throw boom("not a " + expectedType.simpleName() + " or Supplier<" + expectedType.simpleName() + ">");
    }
    List<? extends TypeMirror> typeParameters = resolver.typecheck(asDeclared(supplied), expectedType.expectedClass())
        .orElseThrow(f -> boom(f.getMessage()));
    return new ReferencedType<E>(typeParameters, true);
  }

  private ValidationException boom(String message) {
    return errorHandler.apply(expectedType.boom(message));
  }
}
