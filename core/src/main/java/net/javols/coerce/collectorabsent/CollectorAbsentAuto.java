package net.javols.coerce.collectorabsent;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import net.javols.coerce.BasicInfo;
import net.javols.coerce.NonFlagCoercion;
import net.javols.coerce.NonFlagSkew;
import net.javols.compiler.TypeTool;

import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;

import static net.javols.coerce.NonFlagSkew.OPTIONAL;
import static net.javols.coerce.NonFlagSkew.REPEATABLE;
import static net.javols.coerce.NonFlagSkew.REQUIRED;

public class CollectorAbsentAuto {

  private final BasicInfo basicInfo;

  public CollectorAbsentAuto(BasicInfo basicInfo) {
    this.basicInfo = basicInfo;
  }

  public NonFlagCoercion findCoercion() {
    TypeMirror returnType = basicInfo.originalReturnType();
    Optional<Optionalish> opt = Optionalish.unwrap(returnType, tool());
    Optional<TypeMirror> listWrapped = tool().unwrap(List.class, returnType);
    if (opt.isPresent()) {
      Optionalish optional = opt.get();
      // optional match
      ParameterSpec param = basicInfo.constructorParam(optional.liftedType());
      return createCoercion(optional.wrappedType(), optional.extractExpr(param), param, OPTIONAL);
    }
    if (listWrapped.isPresent()) {
      // repeatable match
      ParameterSpec param = basicInfo.constructorParam(returnType);
      return createCoercion(listWrapped.get(), param, REPEATABLE);
    }
    // exact match (-> required)
    ParameterSpec param = basicInfo.constructorParam(returnType);
    return createCoercion(tool().box(returnType), param, REQUIRED);
  }

  private NonFlagCoercion createCoercion(TypeMirror testType, ParameterSpec constructorParam, NonFlagSkew skew) {
    return createCoercion(testType, CodeBlock.of("$N", constructorParam), constructorParam, skew);
  }

  private NonFlagCoercion createCoercion(TypeMirror testType, CodeBlock extractExpr, ParameterSpec constructorParam, NonFlagSkew skew) {
    return basicInfo.findAutoMapper(testType)
        .map(mapExpr -> new NonFlagCoercion(basicInfo, MapperAttempt.autoCollectExpr(basicInfo, skew), mapExpr, extractExpr, skew, constructorParam))
        .orElseThrow(() -> basicInfo.failure(String.format("Unknown parameter type: %s. Try defining a custom mapper or collector.",
            basicInfo.originalReturnType())));
  }

  private TypeTool tool() {
    return basicInfo.tool();
  }
}
