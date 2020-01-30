package net.javols.coerce.collectorabsent;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import net.javols.coerce.BasicInfo;
import net.javols.coerce.Coercion;
import net.javols.coerce.Skew;
import net.javols.coerce.either.Either;
import net.javols.coerce.either.Left;
import net.javols.coerce.either.Right;
import net.javols.compiler.TypeTool;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.javols.coerce.Skew.OPTIONAL;
import static net.javols.coerce.Skew.REQUIRED;
import static net.javols.coerce.either.Either.left;

public class CollectorAbsentExplicit {

  private final TypeElement mapperClass;
  private final BasicInfo basicInfo;

  public CollectorAbsentExplicit(BasicInfo basicInfo, TypeElement mapperClass) {
    this.mapperClass = mapperClass;
    this.basicInfo = basicInfo;
  }

  private List<MapperAttempt> getAttempts() {
    TypeMirror returnType = basicInfo.returnType();
    Optional<Optionalish> opt = Optionalish.unwrap(returnType, tool());
    List<MapperAttempt> attempts = new ArrayList<>();
    opt.ifPresent(optional -> {
      ParameterSpec param = basicInfo.constructorParam(optional.liftedType());
      // optional match
      attempts.add(attempt(optional.wrappedType(), optional.extractExpr(param), param, OPTIONAL));
      // exact match (-> required)
      attempts.add(attempt(optional.liftedType(), optional.extractExpr(param), param, REQUIRED));
    });
    ParameterSpec param = basicInfo.constructorParam(returnType);
    // exact match (-> required)
    attempts.add(exactMatchAttempt(tool().box(returnType), param));
    return attempts;
  }

  public Coercion findCoercion() {
    List<MapperAttempt> attempts = getAttempts();
    Either<String, Coercion> either = left("");
    for (MapperAttempt attempt : attempts) {
      either = attempt.findCoercion(basicInfo);
      if (either instanceof Right) {
        return ((Right<String, Coercion>) either).value();
      }
    }
    throw basicInfo.failure(((Left<String, Coercion>) either).value());
  }

  private TypeTool tool() {
    return basicInfo.tool();
  }

  private MapperAttempt attempt(TypeMirror expectedReturnType, CodeBlock extractExpr, ParameterSpec constructorParam, Skew skew) {
    return new MapperAttempt(expectedReturnType, extractExpr, constructorParam, skew, mapperClass);
  }

  private MapperAttempt exactMatchAttempt(TypeMirror expectedReturnType, ParameterSpec constructorParam) {
    return new MapperAttempt(expectedReturnType, CodeBlock.of("$N", constructorParam), constructorParam, Skew.REQUIRED, mapperClass);
  }
}
