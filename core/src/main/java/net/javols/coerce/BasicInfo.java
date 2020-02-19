package net.javols.coerce;

import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import net.javols.compiler.TypeTool;
import net.javols.compiler.ValidationException;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Coercion input: Information about a single parameter (option or param).
 */
public class BasicInfo {

  private final ExecutableElement sourceMethod;

  private final TypeTool tool;

  private final TransformInfo transformInfo;

  private final String paramName;

  private BasicInfo(ExecutableElement sourceMethod, TypeTool tool, TransformInfo transformInfo) {
    this.sourceMethod = sourceMethod;
    this.tool = tool;
    this.transformInfo = transformInfo;
    this.paramName = sourceMethod.getSimpleName().toString();
  }

  static BasicInfo create(ExecutableElement sourceMethod, TypeTool tool, TransformInfo transformInfo) {
    return new BasicInfo(sourceMethod, tool, transformInfo);
  }

  public ParameterSpec constructorParam(TypeMirror type) {
    return ParameterSpec.builder(TypeName.get(type), sourceMethod.getSimpleName().toString()).build();
  }

  public TypeMirror returnType() {
    return sourceMethod.getReturnType();
  }

  public ValidationException failure(String message) {
    return ValidationException.create(sourceMethod, message);
  }

  public TypeTool tool() {
    return tool;
  }

  public TransformInfo transformInfo() {
    return transformInfo;
  }

  public String paramName() {
    return paramName;
  }
}
