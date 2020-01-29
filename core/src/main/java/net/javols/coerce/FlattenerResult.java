package net.javols.coerce;

import javax.lang.model.type.TypeMirror;
import java.util.List;

public class FlattenerResult {

  private List<TypeMirror> typeParameters;

  FlattenerResult(List<TypeMirror> typeParameters) {
    this.typeParameters = typeParameters;
  }

  public List<TypeMirror> getTypeParameters() {
    return typeParameters;
  }
}
