package net.javols.coerce;

import net.javols.coerce.matching.AutoMatcher;
import net.javols.compiler.TypeTool;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class CoercionProvider {

  public static Coercion getCoercion(
      ExecutableElement sourceMethod,
      TypeElement valueType,
      TypeTool tool) {
    BasicInfo basicInfo = BasicInfo.create(sourceMethod, tool, valueType);
    return new AutoMatcher(basicInfo).findCoercion();
  }
}
