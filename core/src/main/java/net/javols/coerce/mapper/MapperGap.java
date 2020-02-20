package net.javols.coerce.mapper;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.type.TypeMirror;
import java.util.function.Function;

public class MapperGap {

  private final FieldSpec field;

  private final ParameterSpec param;

  public MapperGap(TypeMirror inputType, TypeMirror outputType, String paramName) {
    TypeName type = ParameterizedTypeName.get(ClassName.get(Function.class),
        TypeName.get(inputType), TypeName.get(outputType));
    this.field = FieldSpec.builder(type, paramName + "Mapper").build();
    this.param = ParameterSpec.builder(type, paramName + "Mapper").build();
  }

  public FieldSpec field() {
    return field;
  }

  public ParameterSpec param() {
    return param;
  }

  public String stepInterface() {
    return capitalize(field().name + "Consumer");
  }

  private String capitalize(String s) {
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }
}
