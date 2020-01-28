package net.javols.compiler;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import net.javols.Key;
import net.javols.coerce.Coercion;
import net.javols.coerce.CoercionProvider;
import net.javols.coerce.Skew;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static net.javols.compiler.Constants.NONPRIVATE_ACCESS_MODIFIERS;

public final class Parameter {

  private final String longName;

  private final ExecutableElement sourceMethod;

  private final Coercion coercion;

  private static ParamName findParamName(
      List<Parameter> alreadyCreated,
      ExecutableElement sourceMethod) {
    String methodName = sourceMethod.getSimpleName().toString();
    ParamName result = ParamName.create(methodName);
    for (Parameter param : alreadyCreated) {
      if (param.paramName().enumConstant().equals(result.enumConstant())) {
        return result.append(Integer.toString(alreadyCreated.size()));
      }
    }
    return result;
  }

  private Parameter(
      String longName,
      ExecutableElement sourceMethod,
      Coercion coercion) {
    this.coercion = coercion;
    this.longName = longName;
    this.sourceMethod = sourceMethod;
  }

  public FieldSpec field() {
    return coercion.field();
  }

  public Coercion coercion() {
    return coercion;
  }

  static Parameter create(TypeTool tool, List<Parameter> alreadyCreated, ExecutableElement sourceMethod) {
    AnnotationUtil annotationUtil = new AnnotationUtil(tool, sourceMethod);
    Optional<TypeElement> mapperClass = annotationUtil.get(net.javols.Key.class, "mappedBy");
    Key parameter = sourceMethod.getAnnotation(Key.class);
    ParamName name = findParamName(alreadyCreated, sourceMethod);
    Coercion coercion1 = CoercionProvider.nonFlagCoercion(sourceMethod, name, mapperClass, tool);
    return new Parameter(parameter.value(), sourceMethod, coercion1);
  }

  public Optional<String> longName() {
    return Optional.ofNullable(longName);
  }

  public String methodName() {
    return sourceMethod.getSimpleName().toString();
  }

  public TypeName returnType() {
    return TypeName.get(sourceMethod.getReturnType());
  }

  public String enumConstant() {
    return paramName().enumConstant();
  }

  public boolean isRequired() {
    return coercion.getSkew() == Skew.REQUIRED;
  }

  public boolean isOptional() {
    return coercion.getSkew() == Skew.OPTIONAL;
  }

  public ParamName paramName() {
    return coercion.paramName();
  }

  ValidationException validationError(String message) {
    return ValidationException.create(sourceMethod, message);
  }

  public Set<Modifier> getAccessModifiers() {
    return sourceMethod.getModifiers().stream()
        .filter(NONPRIVATE_ACCESS_MODIFIERS::contains)
        .collect(Collectors.toSet());
  }
}
