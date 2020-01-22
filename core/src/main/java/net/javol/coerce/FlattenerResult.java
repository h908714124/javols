package net.javol.coerce;

import net.javol.coerce.either.Either;
import net.javol.coerce.reference.TypecheckFailure;
import net.javol.compiler.TypevarMapping;

import javax.lang.model.type.TypeMirror;
import java.util.List;

public class FlattenerResult {

  private List<TypeMirror> typeParameters;

  private TypevarMapping merged;

  FlattenerResult(List<TypeMirror> typeParameters, TypevarMapping merged) {
    this.typeParameters = typeParameters;
    this.merged = merged;
  }

  public List<TypeMirror> getTypeParameters() {
    return typeParameters;
  }

  public Either<TypecheckFailure, TypeMirror> substitute(TypeMirror input) {
    return merged.substitute(input);
  }
}
