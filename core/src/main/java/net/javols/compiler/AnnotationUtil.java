package net.javols.compiler;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import java.util.Map;
import java.util.Optional;

class AnnotationUtil {

  private final TypeTool tool;
  private final String attributeName;
  private final Element annotatedElement;
  private final Class<?> annotationType;

  private static final AnnotationValueVisitor<TypeMirror, Void> GET_TYPE = new SimpleAnnotationValueVisitor8<TypeMirror, Void>() {

    @Override
    public TypeMirror visitType(TypeMirror mirror, Void _null) {
      return mirror;
    }
  };

  AnnotationUtil(TypeTool tool, Element annotatedElement, Class<?> annotationType, String attributeName) {
    this.tool = tool;
    this.annotatedElement = annotatedElement;
    this.annotationType = annotationType;
    this.attributeName = attributeName;
  }

  Optional<TypeElement> getAttributeValue() {
    AnnotationMirror annotation = getAnnotationMirror();
    if (annotation == null) {
      // if the source method doesn't have this annotation
      return Optional.empty();
    }
    AnnotationValue annotationValue = getAnnotationValue(annotation);
    if (annotationValue == null) {
      // if the default value is not overridden
      return Optional.empty();
    }
    TypeMirror typeMirror = annotationValue.accept(GET_TYPE, null);
    if (typeMirror == null) {
      throw ValidationException.create(annotatedElement, String.format("Invalid value of attribute '%s'.", "mappedBy"));
    }
    if (tool.isObject(typeMirror)) {
      // if the default value is not overridden
      return Optional.empty();
    }
    return Optional.of(tool.asTypeElement(typeMirror));
  }

  private AnnotationMirror getAnnotationMirror() {
    for (AnnotationMirror m : annotatedElement.getAnnotationMirrors()) {
      if (tool.isSameType(m.getAnnotationType(), annotationType)) {
        return m;
      }
    }
    return null;
  }

  private AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror) {
    Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
      String simpleName = entry.getKey().getSimpleName().toString();
      if (simpleName.equals(attributeName)) {
        return entry.getValue();
      }
    }
    return null;
  }
}
