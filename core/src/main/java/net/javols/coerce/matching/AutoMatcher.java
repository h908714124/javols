package net.javols.coerce.matching;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import net.javols.coerce.Coercion;
import net.javols.coerce.Skew;
import net.javols.coerce.mapper.MapperGap;
import net.javols.compiler.TypeTool;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

import static net.javols.coerce.Skew.OPTIONAL;

public class AutoMatcher {

  private final ExecutableElement sourceMethod;
  private final TypeTool tool;
  private final TypeElement dataType;
  private final String paramName;

  public AutoMatcher(ExecutableElement sourceMethod, TypeTool tool, TypeElement dataType) {
    this.sourceMethod = sourceMethod;
    this.tool = tool;
    this.dataType = dataType;
    this.paramName = sourceMethod.getSimpleName().toString();
  }

  public Coercion match() {
    TypeMirror returnType = sourceMethod.getReturnType();
    Optional<Optionalish> opt = Optionalish.unwrap(returnType, tool);
    if (opt.isPresent()) {
      Optionalish optional = opt.get();
      // optional match
      ParameterSpec param = constructorParam(optional.liftedType());
      return createCoercion(optional.wrappedType(), optional.extractExpr(param), param, OPTIONAL);
    }
    // exact match (-> required)
    ParameterSpec param = constructorParam(returnType);
    return createCoercion(tool.box(returnType), CodeBlock.of("$N", param), param, Skew.REQUIRED);
  }

  private Coercion createCoercion(TypeMirror keyType, CodeBlock extractExpr, ParameterSpec constructorParam, Skew skew) {
    MapperGap gap = new MapperGap(dataType.asType(), keyType, paramName);
    return new Coercion(gap, extractExpr, skew, constructorParam);
  }

  private ParameterSpec constructorParam(TypeMirror type) {
    return ParameterSpec.builder(TypeName.get(type), "_" + sourceMethod.getSimpleName().toString()).build();
  }
}
