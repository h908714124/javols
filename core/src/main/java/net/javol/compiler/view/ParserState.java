package net.javol.compiler.view;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import net.javol.compiler.Context;
import net.javol.compiler.Parameter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.squareup.javapoet.TypeName.INT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static net.javol.coerce.Util.addBreaks;
import static net.javol.compiler.Constants.STRING;

/**
 * Defines the inner class ParserState
 */
final class ParserState {

  private final Context context;

  private final FieldSpec optionNamesField;

  private final FieldSpec optionParsersField;

  private final FieldSpec paramParsersField;

  private final MethodSpec tryReadOptionMethod;

  private ParserState(
      Context context,
      FieldSpec optionNamesField,
      FieldSpec optionParsersField,
      FieldSpec paramParsersField,
      MethodSpec tryReadOptionMethod) {
    this.context = context;
    this.optionNamesField = optionNamesField;
    this.optionParsersField = optionParsersField;
    this.paramParsersField = paramParsersField;
    this.tryReadOptionMethod = tryReadOptionMethod;
  }

  static ParserState create(Context context, OptionEnum optionEnum) {

    // read-only lookups
    FieldSpec optionNamesField = FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Map.class),
        STRING, context.optionType()), "optionNames")
        .initializer("$T.$N()", context.optionType(), optionEnum.optionNamesMethod())
        .build();

    // stateful parsers
    FieldSpec optionParsersField = FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Map.class),
        context.optionType(), context.optionParserType()), "optionParsers")
        .initializer("$T.$N()", context.optionType(), optionEnum.optionParsersMethod())
        .build();

    FieldSpec paramParsersField = FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(List.class),
        context.repeatableParamParserType()), "paramParsers")
        .initializer("$T.$N()", context.optionType(), optionEnum.paramParsersMethod())
        .build();

    MethodSpec readRegularOptionMethod = tryReadOptionMethod(context, optionNamesField);

    return new ParserState(
        context,
        optionNamesField,
        optionParsersField,
        paramParsersField,
        readRegularOptionMethod);
  }

  TypeSpec define() {
    return TypeSpec.classBuilder(context.parserStateType())
        .addModifiers(PRIVATE, STATIC)
        .addMethod(buildMethod())
        .addMethod(tryReadOptionMethod)
        .addFields(Arrays.asList(optionNamesField, optionParsersField, paramParsersField))
        .build();
  }

  private static MethodSpec tryReadOptionMethod(Context context, FieldSpec optionNamesField) {
    ParameterSpec token = ParameterSpec.builder(STRING, "token").build();
    ParameterSpec index = ParameterSpec.builder(INT, "index").build();

    CodeBlock.Builder code = CodeBlock.builder();
    code.add("if ($N.length() <= 1 || $N.charAt(0) != '-')\n", token, token).indent()
        .addStatement("return null").unindent();

    code.add("if ($N.charAt(1) != '-')\n", token).indent()
        .addStatement("return $N.get($N.substring(0, 2))", optionNamesField, token).unindent();

    code.addStatement("$T $N = $N.indexOf('=')", INT, index, token)
        .addStatement("return $N.get($N.substring(0, $N < 0 ? $N.length() : $N))",
            optionNamesField, token, index, token, index);

    return MethodSpec.methodBuilder("tryReadOption")
        .addParameter(token)
        .addCode(code.build())
        .returns(context.optionType()).build();
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

  MethodSpec tryReadOption() {
    return tryReadOptionMethod;
  }

  FieldSpec parsersField() {
    return optionParsersField;
  }

  FieldSpec positionalParsersField() {
    return paramParsersField;
  }
}
