package net.javols.coerce;

import net.javols.coerce.matching.AutoMatcher;
import net.javols.coerce.matching.MapperMatcher;
import net.javols.compiler.TypeTool;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Optional;

public class CoercionProvider {

  public static Coercion getCoercion(
      ExecutableElement sourceMethod,
      Optional<TypeElement> mapperClass,
      TransformInfo transformInfo,
      TypeTool tool) {
    BasicInfo basicInfo = BasicInfo.create(mapperClass, sourceMethod, tool, transformInfo);
    if (basicInfo.mapperClass().isPresent()) {
      return new MapperMatcher(basicInfo, basicInfo.mapperClass().get()).findCoercion();
    } else {
      if (!tool.isSameType(basicInfo.transformInfo().outputType(), String.class)) {
        throw basicInfo.failure("Define a custom mapper for this key.");
      }
      return new AutoMatcher(basicInfo).findCoercion();
    }
  }
}
