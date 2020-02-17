package net.javols.coerce;

import com.squareup.javapoet.CodeBlock;
import net.javols.coerce.reference.ReferenceTool;
import net.javols.coerce.reference.ReferencedType;
import net.javols.compiler.TypeTool;
import net.javols.compiler.ValidationException;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.function.Function;

import static net.javols.coerce.SuppliedClassValidator.commonChecks;
import static net.javols.coerce.Util.checkNotAbstract;

class TransformInfo {

  private final TypeMirror inputType;

  private final TypeMirror outputType;

  private final CodeBlock createTransform;

  static TransformInfo checkTransform(Function<String, ValidationException> errorHandler, TypeTool tool, TypeElement transformClass) {
    commonChecks(transformClass);
    checkNotAbstract(transformClass);
    if (!transformClass.getTypeParameters().isEmpty()) {
      throw ValidationException.create(transformClass, "The transform class may not have any type parameters.");
    }
    ReferencedType functionType = new ReferenceTool(errorHandler, tool, transformClass).getReferencedType();
    TypeMirror inputType = functionType.typeArguments().get(0);
    TypeMirror outputType = functionType.typeArguments().get(1);
    CodeBlock.Builder createTransform = CodeBlock.builder();
    createTransform.add("new $T()", transformClass);
    if (functionType.isSupplier()) {
      createTransform.add(".get()");
    }
    return new TransformInfo(inputType, outputType, createTransform.build());
  }

  TransformInfo(TypeMirror inputType, TypeMirror outputType, CodeBlock createTransform) {
    this.inputType = inputType;
    this.outputType = outputType;
    this.createTransform = createTransform;
  }

  TypeMirror inputType() {
    return inputType;
  }

  TypeMirror outputType() {
    return outputType;
  }

  CodeBlock createTransform() {
    return createTransform;
  }
}
