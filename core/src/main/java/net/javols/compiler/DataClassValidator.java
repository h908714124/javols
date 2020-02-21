package net.javols.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class DataClassValidator {

  private final TypeTool tool;

  DataClassValidator(TypeTool tool) {
    this.tool = tool;
  }

  Optional<ExecutableElement> runChecks(TypeElement sourceElement) {
    if (sourceElement.getNestingKind().isNested() && sourceElement.getNestingKind() != NestingKind.MEMBER) {
      throw ValidationException.create(sourceElement, "Use a top level class or static inner class.");
    }
    if (sourceElement.getNestingKind().isNested() &&
        !sourceElement.getModifiers().contains(Modifier.STATIC)) {
      throw ValidationException.create(sourceElement, "The nested class must be static.");
    }
    if (sourceElement.getModifiers().contains(Modifier.PRIVATE)) {
      throw ValidationException.create(sourceElement, "The class may not be private.");
    }
    if (sourceElement.getKind() == ElementKind.INTERFACE) {
      throw ValidationException.create(sourceElement, "Use a class, not an interface.");
    }
    getEnclosingElements(sourceElement).forEach(element -> {
      if (element.getModifiers().contains(Modifier.PRIVATE)) {
        throw ValidationException.create(element, "The class may not not be private.");
      }
    });
    if (!tool.isSameType(sourceElement.getSuperclass(), Object.class) ||
        !sourceElement.getInterfaces().isEmpty()) {
      throw ValidationException.create(sourceElement, "The model class may not implement or extend anything.");
    }
    if (!sourceElement.getTypeParameters().isEmpty()) {
      throw ValidationException.create(sourceElement, "The class cannot have type parameters.");
    }
    return getConstructor(sourceElement);
  }

  private static List<TypeElement> getEnclosingElements(TypeElement sourceElement) {
    List<TypeElement> result = new ArrayList<>();
    TypeElement current = sourceElement;
    result.add(current);
    while (current.getNestingKind() == NestingKind.MEMBER) {
      Element enclosingElement = current.getEnclosingElement();
      if (enclosingElement.getKind() != ElementKind.CLASS) {
        return result;
      }
      current = TypeTool.asTypeElement(enclosingElement);
      result.add(current);
    }
    return result;
  }

  private static Optional<ExecutableElement> getConstructor(TypeElement sourceElement) {
    List<ExecutableElement> constructors = ElementFilter.constructorsIn(sourceElement.getEnclosedElements());
    if (constructors.isEmpty()) {
      return Optional.empty();
    }
    if (constructors.size() >= 2) {
      throw ValidationException.create(sourceElement, "More than one constructor");
    }
    ExecutableElement constructor = constructors.get(0);
    if (constructor.getModifiers().contains(Modifier.PRIVATE)) {
      throw ValidationException.create(sourceElement, "Unreachable constructor");
    }
    if (!constructor.getThrownTypes().isEmpty()) {
      throw ValidationException.create(sourceElement, "The constructor may not declare any exceptions.");
    }
    return Optional.of(constructor);
  }
}
