package net.javols.coerce;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import net.javols.compiler.TypeTool;
import net.javols.compiler.ValidationException;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;

/**
 * Coercion input: Information about a single parameter (option or param).
 */
public class BasicInfo {

  private final ExecutableElement sourceMethod;

  private final TypeTool tool;

  // nullable
  private final TypeElement mapperClass;

  private final TransformInfo transformInfo;

  private BasicInfo(Optional<TypeElement> mapperClass, ExecutableElement sourceMethod, TypeTool tool, TransformInfo transformInfo) {
    this.sourceMethod = sourceMethod;
    this.tool = tool;
    this.mapperClass = mapperClass.orElse(null);
    this.transformInfo = transformInfo;
  }

  static BasicInfo create(Optional<TypeElement> mapperClass, ExecutableElement sourceMethod, TypeTool tool, TransformInfo transformInfo) {
    return new BasicInfo(mapperClass, sourceMethod, tool, transformInfo);
  }

  private boolean isEnumType(TypeMirror mirror) {
    List<? extends TypeMirror> supertypes = tool().getDirectSupertypes(mirror);
    if (supertypes.isEmpty()) {
      // not an enum
      return false;
    }
    TypeMirror superclass = supertypes.get(0);
    if (!tool().isSameErasure(superclass, Enum.class)) {
      // not an enum
      return false;
    }
    return !tool().isPrivateType(mirror);
  }

  public Optional<CodeBlock> findAutoMapper(TypeMirror testType) {
    Optional<CodeBlock> mapExpr = AutoMapper.findAutoMapper(tool(), testType);
    if (mapExpr.isPresent()) {
      return mapExpr;
    }
    if (isEnumType(testType)) {
      return Optional.of(CodeBlock.of("$T::valueOf", testType));
    }
    return Optional.empty();
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

  Optional<TypeElement> mapperClass() {
    return Optional.ofNullable(mapperClass);
  }

  public TransformInfo transformInfo() {
    return transformInfo;
  }
}
