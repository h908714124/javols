package net.javols.compiler.view;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import net.javols.compiler.Context;

import java.util.ArrayList;
import java.util.List;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static net.javols.compiler.Constants.LIST_OF_STRING;
import static net.javols.compiler.Constants.STRING;

/**
 * Generates the inner class ParamParser and its subtypes.
 */
final class ParamParser {

  static List<TypeSpec> define(Context context) {
    FieldSpec values = FieldSpec.builder(LIST_OF_STRING, "values")
        .initializer("new $T<>()", ArrayList.class)
        .build();
    List<TypeSpec> result = new ArrayList<>();
    result.add(TypeSpec.classBuilder(context.repeatableParamParserType())
        .addField(values)
        .addMethod(readMethodRepeatable())
        .addModifiers(PRIVATE, STATIC)
        .build());
    if (!context.positionalParams().isEmpty()) {
      result.add(TypeSpec.classBuilder(context.regularParamParserType())
          .superclass(context.repeatableParamParserType())
          .addMethod(readMethodRegular())
          .addModifiers(PRIVATE, STATIC).build());
    }
    return result;
  }

  private static MethodSpec readMethodRepeatable() {
    ParameterSpec valueParam = ParameterSpec.builder(STRING, "value").build();
    return MethodSpec.methodBuilder("read")
        .addParameter(valueParam)
        .returns(TypeName.INT)
        .addStatement("values.add($N)", valueParam)
        .addStatement("return $L", 0)
        .build();
  }

  private static MethodSpec readMethodRegular() {
    ParameterSpec valueParam = ParameterSpec.builder(STRING, "value").build();
    return MethodSpec.methodBuilder("read")
        .addParameter(valueParam)
        .returns(TypeName.INT)
        .addStatement("values.add($N)", valueParam)
        .addStatement("return $L", 1)
        .build();
  }
}
