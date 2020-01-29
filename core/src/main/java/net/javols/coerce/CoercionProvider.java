package net.javols.coerce;

import net.javols.coerce.collectorabsent.CollectorAbsentAuto;
import net.javols.coerce.collectorabsent.CollectorAbsentExplicit;
import net.javols.compiler.ParamName;
import net.javols.compiler.TypeTool;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Optional;

public class CoercionProvider {

  public static Coercion nonFlagCoercion(
      ExecutableElement sourceMethod,
      Optional<TypeElement> mapperClass,
      TypeTool tool) {
    BasicInfo basicInfo = BasicInfo.create(mapperClass, paramName, sourceMethod, tool);
    if (basicInfo.mapperClass().isPresent()) {
      return new CollectorAbsentExplicit(basicInfo, basicInfo.mapperClass().get()).findCoercion();
    } else {
      return new CollectorAbsentAuto(basicInfo).findCoercion();
    }
  }
}
