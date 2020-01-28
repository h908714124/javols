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

  Context(
      TypeElement sourceElement,
      ClassName generatedClass,
      List<Parameter> parameters) {
    this.sourceElement = sourceElement;
    this.generatedClass = generatedClass;
    this.parameters = parameters;
  }

  public ClassName optionParserType() {
    return generatedClass.nestedClass("OptionParser");
  }

  public ClassName repeatableParamParserType() {
    return generatedClass.nestedClass("ParamParser");
  }

  public ClassName flagParserType() {
    return generatedClass.nestedClass("FlagParser");
  }

  public ClassName regularOptionParserType() {
    return generatedClass.nestedClass("RegularOptionParser");
  }

  public ClassName regularParamParserType() {
    return generatedClass.nestedClass("RegularParamParser");
  }

  public ClassName parserStateType() {
    return generatedClass.nestedClass("ParserState");
  }

  public ClassName implType() {
    return generatedClass.nestedClass(sourceElement.getSimpleName() + "Impl");
  }

  public ClassName parseResultType() {
    return generatedClass.nestedClass("ParseResult");
  }

  public ClassName parsingSuccessType() {
    return generatedClass.nestedClass("ParsingSuccess");
  }

  public ClassName parsingFailedType() {
    return generatedClass.nestedClass("ParsingFailed");
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

  public List<Parameter> parameters() {
    return parameters;
  }
}
