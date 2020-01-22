package net.javol.coerce.collectorabsent;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import net.javol.coerce.BasicInfo;
import net.javol.coerce.Coercion;
import net.javol.coerce.NonFlagSkew;
import net.javol.coerce.either.Either;
import net.javol.coerce.either.Left;
import net.javol.coerce.either.Right;
import net.javol.compiler.TypeTool;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.javol.coerce.NonFlagSkew.OPTIONAL;
import static net.javol.coerce.NonFlagSkew.REPEATABLE;
import static net.javol.coerce.NonFlagSkew.REQUIRED;
import static net.javol.coerce.either.Either.left;

public class CollectorAbsentExplicit {

  private final TypeElement mapperClass;
  private final BasicInfo basicInfo;

  public CollectorAbsentExplicit(BasicInfo basicInfo, TypeElement mapperClass) {
    this.mapperClass = mapperClass;
    this.basicInfo = basicInfo;
  }

  private List<MapperAttempt> getAttempts() {
    TypeMirror returnType = basicInfo.originalReturnType();
    Optional<Optionalish> opt = Optionalish.unwrap(returnType, tool());
    Optional<TypeMirror> listWrapped = tool().unwrap(List.class, returnType);
    List<MapperAttempt> attempts = new ArrayList<>();
    opt.ifPresent(optional -> {
      ParameterSpec param = basicInfo.constructorParam(optional.liftedType());
      // optional match
      attempts.add(attempt(optional.wrappedType(), optional.extractExpr(param), param, OPTIONAL));
      // exact match (-> required)
      attempts.add(attempt(optional.liftedType(), optional.extractExpr(param), param, REQUIRED));
    });
    listWrapped.ifPresent(wrapped -> {
      ParameterSpec param = basicInfo.constructorParam(returnType);
      // list match
      attempts.add(attempt(wrapped, param, REPEATABLE));
    });
    ParameterSpec param = basicInfo.constructorParam(returnType);
    // exact match (-> required)
    attempts.add(attempt(tool().box(returnType), param, REQUIRED));
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

  private MapperAttempt attempt(TypeMirror expectedReturnType, CodeBlock extractExpr, ParameterSpec constructorParam, NonFlagSkew skew) {
    return new MapperAttempt(expectedReturnType, extractExpr, constructorParam, skew, mapperClass);
  }

  private MapperAttempt attempt(TypeMirror expectedReturnType, ParameterSpec constructorParam, NonFlagSkew skew) {
    return new MapperAttempt(expectedReturnType, CodeBlock.of("$N", constructorParam), constructorParam, skew, mapperClass);
  }
}
