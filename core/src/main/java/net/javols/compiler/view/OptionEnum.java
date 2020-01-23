package net.javols.compiler.view;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import net.javols.compiler.Context;
import net.javols.compiler.Parameter;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.squareup.javapoet.ParameterSpec.builder;
import static com.squareup.javapoet.TypeSpec.anonymousClassBuilder;
import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static net.javols.compiler.Constants.LIST_OF_STRING;
import static net.javols.compiler.Constants.STRING;

/**
 * Defines the *_Parser.Option enum.
 *
 * @see GeneratedClass
 */
final class OptionEnum {

  private final Context context;

  private final FieldSpec descriptionField;

  private final FieldSpec namesField;

  private final FieldSpec bundleKeyField;

  private final MethodSpec optionNamesMethod;

  private final MethodSpec optionParsersMethod;

  private final MethodSpec paramParsersMethod;

  private final FieldSpec shapeField;

  private OptionEnum(
      Context context,
      FieldSpec bundleKeyField,
      FieldSpec descriptionField,
      FieldSpec namesField,
      MethodSpec optionNamesMethod,
      MethodSpec optionParsersMethod,
      FieldSpec shapeField,
      MethodSpec paramParsersMethod) {
    this.descriptionField = descriptionField;
    this.bundleKeyField = bundleKeyField;
    this.context = context;
    this.optionNamesMethod = optionNamesMethod;
    this.namesField = namesField;
    this.optionParsersMethod = optionParsersMethod;
    this.shapeField = shapeField;
    this.paramParsersMethod = paramParsersMethod;
  }

  static OptionEnum create(Context context) {
    FieldSpec namesField = FieldSpec.builder(LIST_OF_STRING, "names").build();
    FieldSpec bundleKeyField = FieldSpec.builder(STRING, "bundleKey").build();
    FieldSpec descriptionField = FieldSpec.builder(LIST_OF_STRING, "description").build();
    FieldSpec shapeField = FieldSpec.builder(STRING, "shape").build();
    MethodSpec optionNamesMethod = optionNamesMethod(context.optionType(), namesField);
    MethodSpec optionParsersMethod = optionParsersMethod(context);
    MethodSpec paramParsersMethod = paramParsersMethod(context);

    return new OptionEnum(
        context,
        bundleKeyField,
        descriptionField,
        namesField,
        optionNamesMethod,
        optionParsersMethod,
        shapeField,
        paramParsersMethod);
  }

  TypeSpec define() {
    List<Parameter> parameters = context.parameters();
    TypeSpec.Builder spec = TypeSpec.enumBuilder(context.optionType());
    for (Parameter param : parameters) {
      String enumConstant = param.enumConstant();
      spec.addEnumConstant(enumConstant, optionEnumConstant(param));
    }
    return spec.addModifiers(PRIVATE)
        .addField(namesField)
        .addField(bundleKeyField)
        .addField(descriptionField)
        .addField(shapeField)
        .addMethod(missingRequiredMethod())
        .addMethod(privateConstructor())
        .addMethod(optionNamesMethod)
        .addMethod(optionParsersMethod)
        .addMethod(paramParsersMethod)
        .build();
  }

  private TypeSpec optionEnumConstant(Parameter param) {
    Map<String, Object> map = new LinkedHashMap<>();
    CodeBlock names = getNames(param);
    map.put("names", names);
    map.put("bundleKey", param.bundleKey().orElse(null));
    map.put("descExpression", descExpression(param.description()));
    map.put("shape", param.shape());
    String format = String.join(", ",
        "$names:L",
        "$bundleKey:S",
        "$descExpression:L",
        "$shape:S");

    CodeBlock block = CodeBlock.builder().addNamed(format, map).build();
    return anonymousClassBuilder(block).build();
  }

  private CodeBlock getNames(Parameter param) {
    List<String> names = param.names();
    switch (names.size()) {
      case 0:
        return CodeBlock.of("$T.emptyList()", Collections.class);
      case 1:
        return CodeBlock.of("$T.singletonList($S)", Collections.class, names.get(0));
      default:
        return arraysOfStringInvocation(names);
    }
  }

  private CodeBlock descExpression(List<String> desc) {
    switch (desc.size()) {
      case 0:
        return CodeBlock.builder().add("$T.emptyList()", Collections.class).build();
      case 1:
        return CodeBlock.builder().add("$T.singletonList($S)", Collections.class, desc.get(0)).build();
      default:
        return arraysOfStringInvocation(desc);
    }
  }

  private CodeBlock arraysOfStringInvocation(List<String> strings) {
    Object[] args = new Object[1 + strings.size()];
    args[0] = Arrays.class;
    for (int i = 0; i < strings.size(); i++) {
      args[i + 1] = strings.get(i);
    }
    return CodeBlock.of(String.format("$T.asList($Z%s)",
        String.join(",$W", nCopies(strings.size(), "$S"))), args);
  }

  private static MethodSpec optionNamesMethod(ClassName optionType, FieldSpec namesField) {
    ParameterSpec result = builder(ParameterizedTypeName.get(
        ClassName.get(Map.class), STRING, optionType), "result").build();
    ParameterSpec option = builder(optionType, "option").build();
    ParameterSpec name = builder(STRING, "name").build();
    CodeBlock.Builder code = CodeBlock.builder();
    code.addStatement("$T $N = new $T<>($T.values().length)",
        result.type, result, HashMap.class, option.type);

    code.add("for ($T $N : $T.values())\n", option.type, option, option.type).indent()
        .addStatement("$N.$N.forEach($N -> $N.put($N, $N))", option, namesField, name, result, name, option)
        .unindent();
    code.addStatement("return $N", result);

    return MethodSpec.methodBuilder("optionNames").returns(result.type)
        .addCode(code.build())
        .addModifiers(STATIC)
        .build();
  }

  private static MethodSpec optionParsersMethod(Context context) {
    ParameterSpec parsers = builder(ParameterizedTypeName.get(ClassName.get(Map.class), context.optionType(), context.optionParserType()), "parsers").build();

    return MethodSpec.methodBuilder("optionParsers")
        .returns(parsers.type)
        .addCode(optionParsersMethodCode(context, parsers))
        .addModifiers(STATIC).build();
  }

  private static CodeBlock optionParsersMethodCode(Context context, ParameterSpec parsers) {
    List<Parameter> options = context.options();
    if (options.isEmpty()) {
      return CodeBlock.builder().addStatement("return $T.emptyMap()", Collections.class).build();
    }
    if (options.size() == 1) {
      Parameter param = options.get(0);
      return CodeBlock.builder().addStatement("return $T.singletonMap($L, new $T())",
          Collections.class, param.enumConstant(), optionParserType(context, param)).build();
    }
    CodeBlock.Builder code = CodeBlock.builder();
    code.addStatement("$T $N = new $T<>($T.class)",
        parsers.type, parsers, EnumMap.class, context.optionType());
    for (Parameter param : options) {
      code.addStatement("$N.put($L, new $T())", parsers, param.enumConstant(), optionParserType(context, param));
    }
    code.addStatement("return $N", parsers);
    return code.build();
  }

  private static ClassName optionParserType(Context context, Parameter param) {
    if (param.isRepeatable()) {
      return context.optionParserType();
    }
    if (param.isFlag()) {
      return context.flagParserType();
    }
    return context.regularOptionParserType();
  }

  private static MethodSpec paramParsersMethod(Context context) {
    ParameterSpec parsers = builder(ParameterizedTypeName.get(ClassName.get(List.class), context.repeatableParamParserType()), "parsers").build();
    CodeBlock code = paramParsersMethodCode(context);
    return MethodSpec.methodBuilder("paramParsers")
        .returns(parsers.type)
        .addModifiers(STATIC)
        .addStatement(code)
        .build();
  }

  private static CodeBlock paramParsersMethodCode(Context context) {
    List<Parameter> params = context.positionalParams();
    if (params.isEmpty()) {
      return CodeBlock.of("return $T.emptyList()", Collections.class);
    }
    if (params.size() == 1) {
      Parameter param = params.get(0);
      return CodeBlock.of("return $T.singletonList(new $T())", Collections.class, param.isRepeatable() ? context.repeatableParamParserType() : context.regularParamParserType());
    }
    CodeBlock.Builder code = CodeBlock.builder();
    code.add("return $T.asList(", Arrays.class);
    for (int i = 0; i < params.size(); i++) {
      Parameter param = params.get(i);
      code.add("new $T()", param.isRepeatable() ? context.repeatableParamParserType() : context.regularParamParserType());
      if (i < params.size() - 1) {
        code.add(",$W");
      }
    }
    return code.add(")").build();
  }

  private MethodSpec privateConstructor() {
    ParameterSpec names = builder(namesField.type, namesField.name).build();
    ParameterSpec bundleKey = builder(bundleKeyField.type, bundleKeyField.name).build();
    ParameterSpec description = builder(descriptionField.type, descriptionField.name).build();
    ParameterSpec shape = builder(shapeField.type, shapeField.name).build();
    return MethodSpec.constructorBuilder()
        .addStatement("this.$N = $N", namesField, names)
        .addStatement("this.$N = $N", bundleKeyField, bundleKey)
        .addStatement("this.$N = $N", descriptionField, description)
        .addStatement("this.$N = $N", shapeField, shape)
        .addParameters(asList(names, bundleKey, description, shape))
        .build();
  }

  private MethodSpec missingRequiredMethod() {
    CodeBlock.Builder code = CodeBlock.builder()
        .add("return new $T($S + name() +\n", RuntimeException.class, "Missing required: ").indent()
        .addStatement("(names.isEmpty() ? $S : $S + $T.join($S, names) + $S))", "", " (", String.class, ", ", ")").unindent();
    return MethodSpec.methodBuilder("missingRequired")
        .returns(RuntimeException.class)
        .addCode(code.build())
        .build();
  }

  MethodSpec optionNamesMethod() {
    return optionNamesMethod;
  }

  MethodSpec optionParsersMethod() {
    return optionParsersMethod;
  }

  MethodSpec paramParsersMethod() {
    return paramParsersMethod;
  }
}
