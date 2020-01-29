package net.javols.coerce.collectorabsent;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import net.javols.coerce.BasicInfo;
import net.javols.coerce.Coercion;
import net.javols.coerce.MapperClassValidator;
import net.javols.coerce.Skew;
import net.javols.coerce.either.Either;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.function.Function;

class MapperAttempt {

  private final CodeBlock extractExpr;
  private final ParameterSpec constructorParam;
  private final Skew skew;
  private final TypeMirror testType;
  private final TypeElement mapperClass;

  MapperAttempt(TypeMirror testType, CodeBlock extractExpr, ParameterSpec constructorParam, Skew skew, TypeElement mapperClass) {
    this.testType = testType;
    this.extractExpr = extractExpr;
    this.constructorParam = constructorParam;
    this.skew = skew;
    this.mapperClass = mapperClass;
  }

  static CodeBlock autoCollectExpr(BasicInfo basicInfo, Skew skew) {
    switch (skew) {
      case OPTIONAL:
        return CodeBlock.builder().build();
      case REQUIRED:
        return CodeBlock.of("orElseThrow(() -> new $T($S))",
            "Missing: " + basicInfo.parameterName());
      default:
        throw new AssertionError("unknown skew: " + skew);
    }
  }

  Either<String, Coercion> findCoercion(BasicInfo basicInfo) {
    return new MapperClassValidator(basicInfo::failure, basicInfo.tool(), testType, mapperClass).checkReturnType()
        .map(Function.identity(), mapExpr ->
            new Coercion(basicInfo, mapExpr, extractExpr, skew, constructorParam));
  }
}
