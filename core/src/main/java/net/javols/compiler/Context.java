package net.javols.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.List;

import static net.javols.compiler.Constants.NONPRIVATE_ACCESS_MODIFIERS;

public final class Context {

  // the annotated class
  private final TypeElement sourceElement;

  // the class that will be generated
  private final ClassName generatedClass;

  // the abstract methods in the annotated class
  private final List<Parameter> parameters;

  // from annotation
  private final TypeElement dataType;

  Context(
      TypeElement sourceElement,
      ClassName generatedClass,
      List<Parameter> parameters,
      TypeElement dataType) {
    this.sourceElement = sourceElement;
    this.generatedClass = generatedClass;
    this.parameters = parameters;
    this.dataType = dataType;
  }

  public ClassName implType() {
    return generatedClass.nestedClass(sourceElement.getSimpleName() + "Impl");
  }

  public TypeName sourceType() {
    return TypeName.get(sourceElement.asType());
  }

  public Modifier[] getAccessModifiers() {
    return sourceElement.getModifiers().stream()
        .filter(NONPRIVATE_ACCESS_MODIFIERS::contains)
        .toArray(Modifier[]::new);
  }

  public ClassName generatedClass() {
    return generatedClass;
  }

  public ClassName builderClass() {
    return generatedClass.nestedClass("Builder");
  }

  public List<Parameter> parameters() {
    return parameters;
  }

  public TypeElement dataType() {
    return dataType;
  }
}
