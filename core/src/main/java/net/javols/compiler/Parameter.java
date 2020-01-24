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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import static net.javols.compiler.Constants.NONPRIVATE_ACCESS_MODIFIERS;

/**
 * This class represents a parameter method (option or param).
 */
public final class Parameter {

  // null if absent
  private final String longName;

  private final ExecutableElement sourceMethod;

  private final String shape;

  private final List<String> names;

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
      String shape,
      List<String> names,
      Coercion coercion) {
    this.shape = shape;
    this.names = names;
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
    return createPositional(alreadyCreated, sourceMethod, mapperClass, tool);
  }

  private static Parameter createPositional(
      List<Parameter> alreadyCreated,
      ExecutableElement sourceMethod,
      int positionalIndex,
      Optional<TypeElement> mapperClass,
      TypeTool tool) {
    Key parameter = sourceMethod.getAnnotation(Key.class);
    ParamName name = findParamName(alreadyCreated, sourceMethod);
    Coercion coercion = CoercionProvider.nonFlagCoercion(sourceMethod, name, mapperClass, tool);
    return new Parameter(
        null,
        null,
        sourceMethod,
        name.snake().toLowerCase(Locale.US),
        Collections.emptyList(),
        coercion);
  }

  public Optional<String> longName() {
    return Optional.ofNullable(longName);
  }

  public List<String> description() {
    return description;
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

  public boolean isPositional() {
    return positionalIndex != null;
  }

  public boolean isNotPositional() {
    return !isPositional();
  }

  public OptionalInt positionalIndex() {
    return positionalIndex != null ? OptionalInt.of(positionalIndex) : OptionalInt.empty();
  }

  public boolean isRequired() {
    return coercion.getSkew() == Skew.REQUIRED;
  }

  public boolean isRepeatable() {
    return coercion.getSkew() == Skew.REPEATABLE;
  }

  public boolean isOptional() {
    return coercion.getSkew() == Skew.OPTIONAL;
  }

  public boolean isFlag() {
    return coercion.getSkew() == Skew.FLAG;
  }

  public Optional<String> bundleKey() {
    return bundleKey.isEmpty() ? Optional.empty() : Optional.of(bundleKey);
  }

  OptionalInt positionalOrder() {
    if (positionalIndex == null) {
      return OptionalInt.empty();
    }
    if (isRepeatable()) {
      return OptionalInt.of(2);
    }
    return isOptional() ? OptionalInt.of(1) : OptionalInt.of(0);
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

  public List<String> names() {
    return names;
  }

  static List<String> names(String longName, String shortName) {
    if (longName != null && shortName == null) {
      return Collections.singletonList(longName);
    } else if (longName == null && shortName != null) {
      return Collections.singletonList(shortName);
    } else if (longName == null) {
      return Collections.emptyList();
    } else {
      return Arrays.asList(shortName, longName);
    }
  }

  public String shape() {
    return shape;
  }

  private static String shape(
      boolean flag,
      ParamName name,
      List<String> names,
      boolean anyMnemonics) {
    if (names.isEmpty() || names.size() >= 3) {
      throw new AssertionError();
    }
    String argname = flag ? "" : ' ' + name.enumConstant();
    if (names.size() == 1) {
      // The padding has the same length as the string "-f, "
      String padding = anyMnemonics ? "    " : "";
      return padding + names.get(0) + argname;
    }
    return names.get(0) + ", " + names.get(1) + argname;
  }
}
