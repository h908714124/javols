package net.javols.coerce;

import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import net.javols.compiler.TypeTool;
import net.javols.compiler.ValidationException;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Coercion input: Information about a single parameter (option or param).
 */
public class BasicInfo {

  private final ExecutableElement sourceMethod;

  private final TypeTool tool;

  private final TypeElement valueType;

  private final String paramName;

  private BasicInfo(ExecutableElement sourceMethod, TypeTool tool, TypeElement valueType) {
    this.sourceMethod = sourceMethod;
    this.tool = tool;
    this.valueType = valueType;
    this.paramName = sourceMethod.getSimpleName().toString();
  }

  static BasicInfo create(ExecutableElement sourceMethod, TypeTool tool, TypeElement valueType) {
    return new BasicInfo(sourceMethod, tool, valueType);
  }

  public ParameterSpec constructorParam(TypeMirror type) {
    return ParameterSpec.builder(TypeName.get(type), sourceMethod.getSimpleName().toString()).build();
  }

  public TypeMirror returnType() {
    return sourceMethod.getReturnType();
  }

  public TypeTool tool() {
    return tool;
  }

  public TypeElement transformInfo() {
    return valueType;
  }

  public String paramName() {
    return paramName;
  }
}
