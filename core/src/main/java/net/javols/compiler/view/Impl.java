package net.javols.compiler.view;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.javols.compiler.Context;
import net.javols.compiler.Parameter;

import java.util.stream.Collectors;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Defines the *_Impl inner class.
 *
 * @see GeneratedClass
 */
final class Impl {

  static TypeSpec define(Context context) {
    TypeSpec.Builder spec = TypeSpec.classBuilder(context.implType())
        .superclass(context.sourceType());
    for (Parameter param : context.parameters()) {
      spec.addField(field(param));
    }
    return spec.addModifiers(PRIVATE, STATIC)
        .addMethod(implConstructor(context))
        .addMethods(context.parameters().stream()
            .map(Impl::parameterMethodOverride)
            .collect(Collectors.toList()))
        .build();
  }

  private static MethodSpec parameterMethodOverride(Parameter param) {
    return MethodSpec.methodBuilder(param.methodName())
        .returns(param.returnType())
        .addModifiers(param.getAccessModifiers())
        .addStatement("return $N", field(param))
        .build();
  }

  private static MethodSpec implConstructor(Context context) {
    MethodSpec.Builder spec = MethodSpec.constructorBuilder();
    for (Parameter p : context.parameters()) {
      spec.addStatement("this.$N = $L", field(p), p.coercion().extractExpr());
      spec.addParameter(p.coercion().constructorParam());
    }
    return spec.build();
  }

  private static FieldSpec field(Parameter param) {
    return FieldSpec.builder(param.returnType(), param.methodName()).build();
  }
}
