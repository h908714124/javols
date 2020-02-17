package net.javols.coerce.matching;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import net.javols.coerce.BasicInfo;
import net.javols.coerce.Coercion;
import net.javols.coerce.Skew;
import net.javols.compiler.TypeTool;

import javax.lang.model.type.TypeMirror;
import java.util.Optional;

import static net.javols.coerce.Skew.OPTIONAL;

public class AutoMatcher {

  private final BasicInfo basicInfo;

  public AutoMatcher(BasicInfo basicInfo) {
    this.basicInfo = basicInfo;
  }

  public Coercion findCoercion() {
    TypeMirror returnType = basicInfo.returnType();
    Optional<Optionalish> opt = Optionalish.unwrap(returnType, tool());
    if (opt.isPresent()) {
      Optionalish optional = opt.get();
      // optional match
      ParameterSpec param = basicInfo.constructorParam(optional.liftedType());
      return createCoercion(optional.wrappedType(), optional.extractExpr(param), param, OPTIONAL);
    }
    // exact match (-> required)
    ParameterSpec param = basicInfo.constructorParam(returnType);
    return createCoercion(tool().box(returnType), CodeBlock.of("$N", param), param, Skew.REQUIRED);
  }

  private Coercion createCoercion(TypeMirror testType, CodeBlock extractExpr, ParameterSpec constructorParam, Skew skew) {
    return basicInfo.findAutoMapper(testType)
        .map(mapExpr -> new Coercion(mapExpr, extractExpr, skew, constructorParam))
        .orElseThrow(() -> basicInfo.failure(String.format("Unknown key type: %s. Try defining a custom mapper.",
            basicInfo.returnType())));
  }

  private TypeTool tool() {
    return basicInfo.tool();
  }
}