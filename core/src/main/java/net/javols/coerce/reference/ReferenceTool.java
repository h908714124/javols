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

public class ReferenceTool {

  private final Resolver resolver;

  private final Function<String, ValidationException> errorHandler;
  private final TypeElement referencedClass;

  public ReferenceTool(Function<String, ValidationException> errorHandler, TypeTool tool, TypeElement referencedClass) {
    this.errorHandler = errorHandler;
    this.referencedClass = referencedClass;
    this.resolver = new Resolver(tool);
  }

  public ReferencedType getReferencedType() {
    return resolver.typecheck(referencedClass, Supplier.class)
        .fold(this::handleNotSupplier, this::handleSupplier);
  }

  private ReferencedType handleNotSupplier(TypecheckFailure failure) {
    if (failure.isFatal()) {
      throw boom(failure.getMessage());
    }
    List<? extends TypeMirror> expected = resolver.typecheck(referencedClass, Function.class)
        .orElseThrow(f -> boom(f.getMessage()));
    return new ReferencedType(expected, false);
  }

  private ReferencedType handleSupplier(List<? extends TypeMirror> typeArguments) {
    TypeMirror supplied = typeArguments.get(0);
    if (supplied.getKind() != TypeKind.DECLARED) {
      throw boom("not a " + ExpectedType.simpleName() + " or Supplier<" + ExpectedType.simpleName() + ">");
    }
    List<? extends TypeMirror> typeParameters = resolver.typecheck(asDeclared(supplied))
        .orElseThrow(f -> boom(f.getMessage()));
    return new ReferencedType(typeParameters, true);
  }

  private ValidationException boom(String message) {
    return errorHandler.apply(ExpectedType.boom(message));
  }
}
