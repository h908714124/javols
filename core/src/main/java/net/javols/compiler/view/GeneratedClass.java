package net.javols.compiler.view;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import net.javols.coerce.Coercion;
import net.javols.coerce.mapper.MapperGap;
import net.javols.compiler.Context;
import net.javols.compiler.Parameter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Generates the *_Parser class.
 */
public final class GeneratedClass {

  private static final String PROJECT_URL = "https://github.com/h908714124/javols";

  private final Context context;

  private static final TypeVariableName X = TypeVariableName.get("X").withBounds(Throwable.class);
  private static final ParameterizedTypeName S2X = ParameterizedTypeName.get(ClassName.get(Function.class),
      TypeName.get(String.class), X);

  private final ParameterSpec f;
  private final ParameterSpec errMissing = ParameterSpec.builder(S2X, "errMissing").build();

  private GeneratedClass(Context context, ParameterSpec f) {
    this.context = context;
    this.f = f;
  }

  public static GeneratedClass create(Context context) {
    ParameterizedTypeName fType = ParameterizedTypeName.get(ClassName.get(Function.class),
        TypeName.get(String.class), TypeName.get(context.valueType().asType()));
    ParameterSpec f = ParameterSpec.builder(fType, "f").build();
    return new GeneratedClass(context, f);
  }

  public TypeSpec define() {
    TypeSpec.Builder spec = TypeSpec.classBuilder(context.generatedClass());

    List<MapperGap> gaps = context.parameters().stream()
        .map(Parameter::coercion)
        .map(Coercion::gap)
        .collect(Collectors.toList());

    spec.addMethod(parseMethodOverload())
        .addMethod(parseMethod())
        .addMethod(constructor(gaps));

    spec.addType(Impl.define(context));

    for (MapperGap gap : gaps) {
      spec.addField(gap.field());
    }

    return spec.addModifiers(context.getAccessModifiers())
        .addJavadoc(javadoc()).build();
  }

  private MethodSpec constructor(List<MapperGap> gaps) {
    MethodSpec.Builder spec = MethodSpec.constructorBuilder().addModifiers(context.getAccessModifiers());
    for (MapperGap gap : gaps) {
      spec.addParameter(gap.param());
      spec.addStatement("this.$N = $N", gap.field(), gap.param());
    }
    return spec.build();
  }

  private MethodSpec parseMethodOverload() {
    ParameterSpec key = ParameterSpec.builder(String.class, "key").build();
    return MethodSpec.methodBuilder("parse")
        .addParameter(f)
        .addStatement("return parse($N, $N -> new $T($S + $N + $S))", f, key, IllegalArgumentException.class,
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
    spec.addStatement("$T $N = $N -> $T.ofNullable($N.apply($N))", a.type, a, key, Optional.class, f, key);
    if (context.parameters().stream().anyMatch(Parameter::isRequired)) {
      spec.addStatement("$T $N = $N -> () -> $N.apply($N)", e.type, e, key, errMissing, key);
    }

    return spec.addParameters(Arrays.asList(f, errMissing))
        .addStatement("return new $T($L)", context.implType(), getBuildExpr(e, a))
        .returns(context.sourceType())
        .addModifiers(context.getAccessModifiers())
        .build();
  }

  private CodeBlock getBuildExpr(ParameterSpec e, ParameterSpec a) {
    CodeBlock.Builder args = CodeBlock.builder().add("\n");
    for (int j = 0; j < context.parameters().size(); j++) {
      Parameter param = context.parameters().get(j);
      args.add(extractExpression(param, a, e));
      if (j < context.parameters().size() - 1) {
        args.add(",\n");
      }
    }
    return args.build();
  }

  private ParameterSpec eParam() {
    TypeName eType = ParameterizedTypeName.get(ClassName.get(Function.class),
        TypeName.get(String.class), ParameterizedTypeName.get(ClassName.get(Supplier.class), X));
    return ParameterSpec.builder(eType, "e").build();
  }

  private ParameterSpec aParam() {
    TypeName aType = ParameterizedTypeName.get(ClassName.get(Function.class),
        TypeName.get(String.class), ParameterizedTypeName.get(ClassName.get(Optional.class),
            ClassName.get(context.valueType())));
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
