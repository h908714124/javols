package net.javols.compiler;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.VariableElement;

public class CarryArg {

  private final String name;
  private final FieldSpec field;
  private final ParameterSpec param;

  public CarryArg(VariableElement variableElement) {
    this.name = variableElement.getSimpleName().toString();
    this.field = FieldSpec.builder(TypeName.get(variableElement.asType()), "$" + name).build();
    this.param = ParameterSpec.builder(TypeName.get(variableElement.asType()), name).build();
  }

  public FieldSpec field() {
    return field;
  }

  public ParameterSpec param() {
    return param;
  }

  public ParameterSpec collisionFreeParam() {
    return ParameterSpec.builder(field.type, "$" + name).build();
  }

  public CodeBlock assignment() {
    return CodeBlock.of("this.$N = $N", field, param);
  }
}
