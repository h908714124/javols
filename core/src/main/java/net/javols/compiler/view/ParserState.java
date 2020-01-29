package net.javols.compiler.view;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import net.javols.compiler.Context;
import net.javols.compiler.Parameter;

import java.util.Map;
import java.util.Optional;

/**
 * Defines the inner class ParserState
 */
final class ParserState {

  private static final ParameterizedTypeName STRING_MAP = ParameterizedTypeName.get(Map.class, String.class, String.class);

  private final Context context;

  private ParserState(Context context) {
    this.context = context;
  }

  private final ParameterSpec m = ParameterSpec.builder(STRING_MAP, "m").build();

  static ParserState create(Context context) {
    return new ParserState(context);
  }

  MethodSpec define() {
    return buildMethod();
  }

  private MethodSpec buildMethod() {

    CodeBlock.Builder args = CodeBlock.builder().add("\n");
    for (int j = 0; j < context.parameters().size(); j++) {
      Parameter param = context.parameters().get(j);
      args.add(extractExpression(param));
      if (j < context.parameters().size() - 1) {
        args.add(",\n");
      }
    }

    return MethodSpec.methodBuilder("build")
        .addParameter(m)
        .addStatement("return new $T($L)", context.implType(), args.build())
        .returns(context.sourceType())
        .build();
  }

  private CodeBlock extractExpression(Parameter param) {
    return CodeBlock.builder().add("$T.ofNullable($N.get($S))", Optional.class, m, param.key())
        .add(".map($L)", param.coercion().mapExpr())
        .add(collectExpr(param))
        .build();
  }

  private CodeBlock collectExpr(Parameter param) {
    if (!param.isRequired()) {
      return CodeBlock.builder().build();
    }
    return CodeBlock.of(".orElseThrow(() -> missingRequired($S))", param.key());
  }
}
