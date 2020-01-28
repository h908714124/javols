package net.javols.compiler.view;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import net.javols.compiler.Context;
import net.javols.compiler.Parameter;

import static net.javols.coerce.Util.addBreaks;

/**
 * Defines the inner class ParserState
 */
final class ParserState {

  private final Context context;

  private ParserState(Context context) {
    this.context = context;
  }

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
        .addStatement("return new $T($L)", context.implType(), args.build())
        .returns(context.sourceType())
        .build();
  }

  private CodeBlock extractExpression(Parameter param) {
    return getStreamExpression(param)
        .add(".values.stream()")
        .add(".map($L)", param.coercion().mapExpr())
        .add(param.coercion().collectExpr())
        .build();
  }

  static CodeBlock throwRepetitionErrorStatement(ParameterSpec optionParam) {
    return CodeBlock.of(addBreaks("throw new $T($T.format($S, $N, $T.join($S, $N.names)))"),
        RuntimeException.class, String.class,
        "Option %s (%s) is not repeatable",
        optionParam, String.class, ", ", optionParam);
  }

  /**
   * @return An expression that extracts the value of the given param from the parser state.
   * This expression will evaluate either to a {@link java.util.stream.Stream} or a {@link java.util.Optional}.
   */
  private CodeBlock.Builder getStreamExpression(Parameter param) {
    if (param.isPositional()) {
      return CodeBlock.builder().add(
          "$N.get($L)", paramParsersField,
          param.positionalIndex().orElseThrow(AssertionError::new));
    }
    return CodeBlock.builder().add(
        "$N.get($T.$N)", optionParsersField,
        context.optionType(), param.enumConstant());
  }
}
