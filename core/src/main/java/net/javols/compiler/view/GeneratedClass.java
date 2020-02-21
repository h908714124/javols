package net.javols.compiler.view;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import net.javols.coerce.Coercion;
import net.javols.coerce.mapper.MapperGap;
import net.javols.compiler.CarryArg;
import net.javols.compiler.Context;
import net.javols.compiler.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Generates the *_Parser class.
 */
public final class GeneratedClass {

  private static final String PROJECT_URL = "https://github.com/h908714124/javols";

  private final Context context;

  private static final TypeVariableName X = TypeVariableName.get("X").withBounds(Throwable.class);
  private static final ParameterizedTypeName S2X = ParameterizedTypeName.get(ClassName.get(Function.class),
      TypeName.get(String.class), X);

  private final ParameterSpec m;
  private final ParameterSpec errMissing = ParameterSpec.builder(S2X, "errMissing").build();

  private GeneratedClass(Context context, ParameterSpec m) {
    this.context = context;
    this.m = m;
  }

  public static GeneratedClass create(Context context) {
    ParameterizedTypeName mType = ParameterizedTypeName.get(ClassName.get(Function.class),
        TypeName.get(String.class), TypeName.get(context.dataType().asType()));
    ParameterSpec m = ParameterSpec.builder(mType, "m").build();
    return new GeneratedClass(context, m);
  }

  public TypeSpec define() {
    TypeSpec.Builder spec = TypeSpec.classBuilder(context.generatedClass());

    List<MapperGap> gaps = context.parameters().stream()
        .map(Parameter::coercion)
        .map(Coercion::gap)
        .collect(Collectors.toList());
    ClassName parserClass = context.generatedClass().nestedClass("Parser");

    spec.addMethod(MethodSpec.methodBuilder("prepare")
        .addParameters(context.carryArgs().stream().map(CarryArg::param).collect(Collectors.toList()))
        .returns(parserClass)
        .addStatement("return new $T($L)", parserClass, context.carryBlock())
        .addModifiers(context.getAccessModifiers())
        .build())
        .addMethod(constructor(gaps));

    TypeSpec.Builder builderSpec = TypeSpec.classBuilder(context.builderClass())
        .addModifiers(PRIVATE, STATIC);
    for (int i = 0; i < gaps.size() - 1; i++) {
      MapperGap gap = gaps.get(i);
      builderSpec.addField(gap.field());
      ClassName stepInterface = context.generatedClass().nestedClass(gaps.get(i).stepInterface());
      MethodSpec stepMethod = MethodSpec.methodBuilder(gaps.get(i).mapperName())
          .addParameter(gaps.get(i).param())
          .returns(context.generatedClass().nestedClass(gaps.get(i + 1).stepInterface()))
          .addModifiers(PUBLIC)
          .build();
      spec.addType(TypeSpec.interfaceBuilder(stepInterface)
          .addModifiers(context.getAccessModifiers())
          .addMethod(stepMethod.toBuilder()
              .addModifiers(ABSTRACT).build()).build());
      builderSpec.addSuperinterface(stepInterface)
          .addMethod(stepMethod.toBuilder()
              .addStatement("this.$N = $N", gaps.get(i).field(), gaps.get(i).param())
              .addStatement("return this")
              .build());
    }
    ClassName stepInterface = context.generatedClass().nestedClass(gaps.get(gaps.size() - 1).stepInterface());
    MethodSpec stepMethod = MethodSpec.methodBuilder(gaps.get(gaps.size() - 1).mapperName())
        .addParameter(gaps.get(gaps.size() - 1).param())
        .returns(context.generatedClass())
        .addModifiers(PUBLIC)
        .build();
    spec.addType(TypeSpec.interfaceBuilder(stepInterface)
        .addModifiers(context.getAccessModifiers())
        .addMethod(stepMethod.toBuilder()
            .addModifiers(ABSTRACT).build()).build());
    builderSpec.addSuperinterface(stepInterface);
    builderSpec.addMethod(stepMethod.toBuilder()
        .addStatement("return new $T($L)", context.generatedClass(), constructorParams(gaps))
        .build());

    spec.addMethod(MethodSpec.methodBuilder("create").addModifiers(STATIC)
        .addModifiers(context.getAccessModifiers())
        .returns(context.generatedClass().nestedClass(gaps.get(0).stepInterface()))
        .addStatement("return new $T()", context.builderClass())
        .build());

    spec.addFields(getFields(gaps));
    spec.addType(builderSpec.build());
    spec.addType(Impl.define(context));
    spec.addType(TypeSpec.classBuilder(parserClass)
        .addModifiers(context.getAccessModifiers())
        .addFields(context.carryArgs().stream().map(CarryArg::field).collect(Collectors.toList()))
        .addMethod(parserConstructor())
        .addMethod(parseMethodOverload())
        .addMethod(parseMethod())
        .build());

    return spec.addModifiers(context.getAccessModifiers())
        .addJavadoc(javadoc()).build();
  }


  private MethodSpec parserConstructor() {
    MethodSpec.Builder spec = MethodSpec.constructorBuilder();
    for (CarryArg carryArgument : context.carryArgs()) {
      spec.addParameter(carryArgument.param());
      spec.addStatement(carryArgument.assignment());
    }
    return spec.addModifiers(PRIVATE).build();
  }

  private List<FieldSpec> getFields(List<MapperGap> gaps) {
    List<FieldSpec> fieldSpecs = new ArrayList<>();
    for (MapperGap gap : gaps) {
      FieldSpec field = gap.field().toBuilder().addModifiers(PRIVATE).build();
      fieldSpecs.add(field);
    }
    return fieldSpecs;
  }

  private CodeBlock constructorParams(List<MapperGap> gaps) {
    CodeBlock.Builder constructorParams = CodeBlock.builder();
    for (int i = 0; i < gaps.size() - 1; i++) {
      MapperGap gap = gaps.get(i);
      constructorParams.add("$N,$Z", gap.field());
    }
    constructorParams.add("$N", gaps.get(gaps.size() - 1).param());
    return constructorParams.build();
  }

  private MethodSpec constructor(List<MapperGap> gaps) {
    MethodSpec.Builder spec = MethodSpec.constructorBuilder()
        .addModifiers(PRIVATE);
    for (MapperGap gap : gaps) {
      spec.addParameter(gap.param());
      spec.addStatement("this.$N = $N", gap.field(), gap.param());
    }
    return spec.build();
  }

  private MethodSpec parseMethodOverload() {
    ParameterSpec key = ParameterSpec.builder(String.class, "key").build();
    return MethodSpec.methodBuilder("parse")
        .addParameter(m)
        .addStatement("return parse($N, $N -> new $T($S + $N + $S))", m, key, IllegalArgumentException.class,
            "Missing required key: <", key, ">")
        .returns(context.sourceType())
        .addModifiers(context.getAccessModifiers())
        .build();
  }

  private MethodSpec parseMethod() {

    ParameterSpec key = ParameterSpec.builder(String.class, "key").build();
    ParameterSpec e = eParam();
    ParameterSpec a = aParam();
    MethodSpec.Builder spec = MethodSpec.methodBuilder("parse");
    spec.addException(X);
    spec.addTypeVariable(X);
    spec.addStatement("$T $N = $N -> $T.ofNullable($N.apply($N))", a.type, a, key, Optional.class, m, key);
    if (context.parameters().stream().anyMatch(Parameter::isRequired)) {
      spec.addStatement("$T $N = $N -> () -> $N.apply($N)", e.type, e, key, errMissing, key);
    }

    return spec.addParameters(Arrays.asList(m, errMissing))
        .addStatement("return new $T($L)", context.implType(), getBuildExpr(e, a))
        .returns(context.sourceType())
        .addModifiers(context.getAccessModifiers())
        .build();
  }

  private CodeBlock getBuildExpr(ParameterSpec e, ParameterSpec a) {
    CodeBlock.Builder code = CodeBlock.builder().add("\n");
    for (CarryArg carryArg : context.carryArgs()) {
      code.add("this.$N,\n", carryArg.field());
    }
    for (int j = 0; j < context.parameters().size(); j++) {
      Parameter param = context.parameters().get(j);
      code.add(extractExpression(param, a, e));
      if (j < context.parameters().size() - 1) {
        code.add(",\n");
      }
    }
    return code.build();
  }

  private ParameterSpec eParam() {
    TypeName eType = ParameterizedTypeName.get(ClassName.get(Function.class),
        TypeName.get(String.class), ParameterizedTypeName.get(ClassName.get(Supplier.class), X));
    return ParameterSpec.builder(eType, "e").build();
  }

  private ParameterSpec aParam() {
    TypeName aType = ParameterizedTypeName.get(ClassName.get(Function.class),
        TypeName.get(String.class), ParameterizedTypeName.get(ClassName.get(Optional.class),
            ClassName.get(context.dataType())));
    return ParameterSpec.builder(aType, "a").build();
  }

  private CodeBlock extractExpression(Parameter param, ParameterSpec a, ParameterSpec e) {
    return CodeBlock.builder().add("$N.apply($S)", a, param.key())
        .add(".map($N)", param.coercion().gap().field())
        .add(collectExpr(param, e)).build();
  }

  private CodeBlock collectExpr(Parameter param, ParameterSpec e) {
    if (!param.isRequired()) {
      return CodeBlock.builder().build();
    }
    return CodeBlock.of(".orElseThrow($N.apply($S))", e, param.key());
  }

  private CodeBlock javadoc() {
    return CodeBlock.builder().add("Generated by <a href=\"" + PROJECT_URL + "\">javols " +
        getClass().getPackage().getImplementationVersion() +
        "</a>\n").build();
  }
}
