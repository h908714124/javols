package net.javols.coerce;

import net.javols.coerce.matching.AutoMatcher;
import net.javols.coerce.matching.MapperMatcher;
import net.javols.compiler.TypeTool;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Optional;

public class CoercionProvider {

  public static Coercion nonFlagCoercion(
      ExecutableElement sourceMethod,
      Optional<TypeElement> mapperClass,
      TypeTool tool) {
    BasicInfo basicInfo = BasicInfo.create(mapperClass, sourceMethod, tool);
    if (basicInfo.mapperClass().isPresent()) {
      return new MapperMatcher(basicInfo, basicInfo.mapperClass().get()).findCoercion();
    } else {
      return new AutoMatcher(basicInfo).findCoercion();
    }
  }
}
