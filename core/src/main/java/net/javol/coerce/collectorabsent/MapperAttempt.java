package net.javol.coerce.collectorabsent;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import net.javol.coerce.BasicInfo;
import net.javol.coerce.Coercion;
import net.javol.coerce.MapperClassValidator;
import net.javol.coerce.NonFlagCoercion;
import net.javol.coerce.NonFlagSkew;
import net.javol.coerce.either.Either;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.function.Function;
import java.util.stream.Collectors;

class MapperAttempt {

  private final CodeBlock extractExpr;
  private final ParameterSpec constructorParam;
  private final NonFlagSkew skew;
  private final TypeMirror testType;
  private final TypeElement mapperClass;

  MapperAttempt(TypeMirror testType, CodeBlock extractExpr, ParameterSpec constructorParam, NonFlagSkew skew, TypeElement mapperClass) {
    this.testType = testType;
    this.extractExpr = extractExpr;
    this.constructorParam = constructorParam;
    this.skew = skew;
    this.mapperClass = mapperClass;
  }

  static CodeBlock autoCollectExpr(BasicInfo basicInfo, NonFlagSkew skew) {
    switch (skew) {
      case OPTIONAL:
        return CodeBlock.of(".findAny()");
      case REQUIRED:
        return CodeBlock.of(".findAny().orElseThrow($T.$L::missingRequired)", basicInfo.optionType(),
            basicInfo.parameterName().enumConstant());
      case REPEATABLE:
        return CodeBlock.of(".collect($T.toList())", Collectors.class);
      default:
        throw new AssertionError("unknown skew: " + skew);
    }
  }

  Either<String, Coercion> findCoercion(BasicInfo basicInfo) {
    return new MapperClassValidator(basicInfo::failure, basicInfo.tool(), testType, mapperClass).checkReturnType()
        .map(Function.identity(), mapperType ->
            new NonFlagCoercion(basicInfo, autoCollectExpr(basicInfo, skew), mapperType, extractExpr, skew, constructorParam));
  }
}
