package net.javols.compiler;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import java.util.Map;
import java.util.Optional;

class AnnotationUtil {

  private final TypeTool tool;
  private final ExecutableElement sourceMethod;

  private static final AnnotationValueVisitor<TypeMirror, Void> GET_TYPE = new SimpleAnnotationValueVisitor8<TypeMirror, Void>() {

    @Override
    public TypeMirror visitType(TypeMirror mirror, Void _null) {
      return mirror;
    }
  };

  AnnotationUtil(TypeTool tool, ExecutableElement sourceMethod) {
    this.tool = tool;
    this.sourceMethod = sourceMethod;
  }

  Optional<TypeElement> getMappedBy() {
    AnnotationMirror annotation = getAnnotationMirror(tool, sourceMethod);
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
      throw ValidationException.create(sourceMethod, String.format("Invalid value of attribute '%s'.", "mappedBy"));
    }
    if (tool.isObject(typeMirror)) {
      // if the default value is not overridden
      return Optional.empty();
    }
    return Optional.of(tool.asTypeElement(typeMirror));
  }

  private static AnnotationMirror getAnnotationMirror(TypeTool tool, ExecutableElement sourceMethod) {
    for (AnnotationMirror m : sourceMethod.getAnnotationMirrors()) {
      if (tool.isSameType(m.getAnnotationType(), net.javols.Key.class)) {
        return m;
      }
    }
    return null;
  }

  private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror) {
    Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
      String simpleName = entry.getKey().getSimpleName().toString();
      if (simpleName.equals("mappedBy")) {
        return entry.getValue();
      }
    }
    return null;
  }
}
