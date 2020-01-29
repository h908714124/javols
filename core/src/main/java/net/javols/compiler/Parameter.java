package net.javols.compiler;

import com.squareup.javapoet.TypeName;
import net.javols.Key;
import net.javols.coerce.Coercion;
import net.javols.coerce.CoercionProvider;
import net.javols.coerce.Skew;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static net.javols.compiler.Constants.NONPRIVATE_ACCESS_MODIFIERS;

public final class Parameter {

  private final String key;

  private final ExecutableElement sourceMethod;

  private final Coercion coercion;

  private Parameter(
      String key,
      ExecutableElement sourceMethod,
      Coercion coercion) {
    this.coercion = coercion;
    this.key = key;
    this.sourceMethod = sourceMethod;
  }

  public Coercion coercion() {
    return coercion;
  }

  static Parameter create(TypeTool tool, ExecutableElement sourceMethod) {
    AnnotationUtil annotationUtil = new AnnotationUtil(tool, sourceMethod);
    Optional<TypeElement> mapperClass = annotationUtil.getMappedBy();
    Key parameter = sourceMethod.getAnnotation(Key.class);
    Coercion coercion = CoercionProvider.nonFlagCoercion(sourceMethod, mapperClass, tool);
    return new Parameter(parameter.value(), sourceMethod, coercion);
  }

  public String key() {
    return key;
  }

  public String methodName() {
    return sourceMethod.getSimpleName().toString();
  }

  public TypeName returnType() {
    return TypeName.get(sourceMethod.getReturnType());
  }

  public boolean isRequired() {
    return coercion.getSkew() == Skew.REQUIRED;
  }

  public boolean isOptional() {
    return coercion.getSkew() == Skew.OPTIONAL;
  }

  public Set<Modifier> getAccessModifiers() {
    return sourceMethod.getModifiers().stream()
        .filter(NONPRIVATE_ACCESS_MODIFIERS::contains)
        .collect(Collectors.toSet());
  }
}
