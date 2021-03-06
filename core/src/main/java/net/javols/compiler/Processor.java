package net.javols.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import net.javols.Data;
import net.javols.Key;
import net.javols.compiler.view.GeneratedClass;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.util.ElementFilter.methodsIn;

public final class Processor extends AbstractProcessor {

  private final boolean debug;

  public Processor() {
    this(false);
  }

  // visible for testing
  Processor(boolean debug) {
    this.debug = debug;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(Data.class, Key.class)
        .map(Class::getCanonicalName)
        .collect(toSet());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
    TypeTool tool = new TypeTool(processingEnv.getElementUtils(), processingEnv.getTypeUtils());
    try {
      getAnnotatedMethods(env, annotations).forEach(method -> {
        checkEnclosingElementIsAnnotated(method);
        validateParameterMethod(method, tool);
      });
    } catch (ValidationException e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.about);
      return false;
    }
    if (annotations.stream().map(TypeElement::getQualifiedName)
        .noneMatch(name -> name.contentEquals(Data.class.getCanonicalName()))) {
      return false;
    }
    ElementFilter.typesIn(env.getElementsAnnotatedWith(Data.class))
        .forEach(sourceElement -> processSourceElement(sourceElement, tool));
    return false;
  }

  private void processSourceElement(TypeElement sourceElement, TypeTool tool) {
    ClassName generatedClass = generatedClass(sourceElement);
    try {
      AnnotationUtil annotationUtil = new AnnotationUtil(tool, sourceElement, Data.class, "value");
      List<CarryArg> carryArgs = new DataClassValidator(tool).runChecks(sourceElement)
          .map(this::getCarryArgs).orElse(Collections.emptyList());
      TypeElement dataType = annotationUtil.getAttributeValue().orElseThrow(AssertionError::new);
      checkDataType(dataType);
      List<Parameter> parameters = getParams(tool, sourceElement, dataType);
      if (parameters.isEmpty()) { // javapoet #739
        throw ValidationException.create(sourceElement, "Define at least one abstract method");
      }
      Context context = new Context(sourceElement, generatedClass, parameters, dataType, carryArgs);
      TypeSpec typeSpec = GeneratedClass.create(context).define();
      write(sourceElement, context.generatedClass(), typeSpec);
    } catch (ValidationException e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.about);
    } catch (AssertionError error) {
      handleUnknownError(sourceElement, error);
    }
  }

  private List<CarryArg> getCarryArgs(ExecutableElement carryConstructor) {
    return carryConstructor.getParameters().stream()
        .map(CarryArg::new)
        .collect(Collectors.toList());

  }

  private static void checkDataType(TypeElement dataType) {
    if (!dataType.getTypeParameters().isEmpty()) {
      throw ValidationException.create(dataType, "The value type may not have any type parameters.");
    }
    if (dataType.getModifiers().contains(Modifier.PRIVATE)) {
      throw ValidationException.create(dataType, "The value type may not be private.");
    }
  }

  private void write(TypeElement sourceElement, ClassName generatedType, TypeSpec definedType) {
    JavaFile.Builder builder = JavaFile.builder(generatedType.packageName(), definedType);
    JavaFile javaFile = builder.build();
    try {
      JavaFileObject sourceFile = processingEnv.getFiler()
          .createSourceFile(generatedType.toString(),
              javaFile.typeSpec.originatingElements.toArray(new Element[0]));
      try (Writer writer = sourceFile.openWriter()) {
        String sourceCode = javaFile.toString();
        writer.write(sourceCode);
        if (debug) {
          Stream.of("##############", "# Debug info #", "##############")
              .forEach(System.err::println);
          System.err.println(sourceCode);
        }
      } catch (IOException e) {
        handleUnknownError(sourceElement, e);
      }
    } catch (IOException e) {
      handleUnknownError(sourceElement, e);
    }
  }

  private List<Parameter> getParams(TypeTool tool, TypeElement sourceElement, TypeElement dataType) {
    List<ExecutableElement> abstractMethods = methodsIn(sourceElement.getEnclosedElements()).stream()
        .filter(method -> method.getModifiers().contains(ABSTRACT))
        .collect(Collectors.toList());
    abstractMethods.forEach(method -> validateParameterMethod(method, tool));
    List<Parameter> result = new ArrayList<>();
    Set<String> keys = new HashSet<>(abstractMethods.size());
    for (ExecutableElement method : abstractMethods) {
      Parameter param = Parameter.create(tool, method, dataType);
      if (!keys.add(param.key())) {
        throw ValidationException.create(method, "Duplicate key: " + param.key());
      }
      result.add(param);
    }
    return result;
  }

  private static void validateParameterMethod(ExecutableElement method, TypeTool tool) {
    if (!method.getModifiers().contains(ABSTRACT)) {
      if (method.getAnnotation(Key.class) != null) {
        throw ValidationException.create(method, "The method must be abstract.");
      }
      return;
    }
    if (!method.getParameters().isEmpty()) {
      throw ValidationException.create(method, "The method may not have parameters.");
    }
    if (!method.getTypeParameters().isEmpty()) {
      throw ValidationException.create(method, "The method may not have type parameters.");
    }
    if (!method.getThrownTypes().isEmpty()) {
      throw ValidationException.create(method, "The method may not declare any exceptions.");
    }
    if (method.getAnnotation(Key.class) == null) {
      throw ValidationException.create(method, String.format("missing @%s annotation",
          Key.class.getSimpleName()));
    }
    if (!tool.isReachable(method.getReturnType())) {
      throw ValidationException.create(method, "Unreachable key type.");
    }
  }

  private List<ExecutableElement> getAnnotatedMethods(RoundEnvironment env, Set<? extends TypeElement> annotations) {
    if (annotations.stream().noneMatch(a ->
        a.getQualifiedName().contentEquals(Key.class.getCanonicalName()))) {
      return Collections.emptyList();
    }
    return new ArrayList<>(methodsIn(env.getElementsAnnotatedWith(Key.class)));
  }

  private static ClassName generatedClass(TypeElement sourceElement) {
    ClassName type = ClassName.get(sourceElement);
    String name = String.join("_", type.simpleNames()) + "_Parser";
    return type.topLevelClassName().peerClass(name);
  }

  private void handleUnknownError(TypeElement sourceType, Throwable e) {
    String message = String.format("JAVOLS: Unexpected error while processing %s: %s", sourceType, e.getMessage());
    e.printStackTrace(System.err);
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, sourceType);
  }

  private void checkEnclosingElementIsAnnotated(ExecutableElement method) {
    Element enclosingElement = method.getEnclosingElement();
    if (enclosingElement.getKind() != ElementKind.CLASS) {
      throw ValidationException.create(enclosingElement, "The enclosing element must be a class.");
    }
    if (enclosingElement.getAnnotation(Data.class) == null) {
      throw ValidationException.create(enclosingElement,
          "The class must have the @" + Data.class.getSimpleName() + " annotation.");
    }
  }
}
