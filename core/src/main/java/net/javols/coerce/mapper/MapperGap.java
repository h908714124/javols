package net.javols.coerce.mapper;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.type.TypeMirror;
import java.util.function.Function;

/**
 * The gap is the (type of the) function that the user needs to supply
 * (at runtime) in order to map any key.
 * It is a function from data type to key type.
 */
public class MapperGap {

  private final FieldSpec field;
  private final ParameterSpec param;
  private final String name;

  public MapperGap(TypeMirror dataType, TypeMirror keyType, String paramName) {
    TypeName type = ParameterizedTypeName.get(ClassName.get(Function.class),
        TypeName.get(dataType), TypeName.get(keyType));
    this.field = FieldSpec.builder(type, "_" + paramName + "Mapper").build();
    this.param = ParameterSpec.builder(type, paramName + "Mapper").build();
    this.name = paramName;
  }

  public FieldSpec field() {
    return field;
  }

  public ParameterSpec param() {
    return param;
  }

  public String stepInterface() {
    return capitalize(mapperName() + "Consumer");
  }

  private String capitalize(String s) {
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  public String mapperName() {
    return name + "Mapper";
  }
}
