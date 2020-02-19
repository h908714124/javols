package net.javols.coerce;

import net.javols.coerce.matching.AutoMatcher;
import net.javols.compiler.TypeTool;

import javax.lang.model.element.ExecutableElement;

public class CoercionProvider {

  public static Coercion getCoercion(
      ExecutableElement sourceMethod,
      TransformInfo transformInfo,
      TypeTool tool) {
    BasicInfo basicInfo = BasicInfo.create(sourceMethod, tool, transformInfo);
    return new AutoMatcher(basicInfo).findCoercion();
  }
}
