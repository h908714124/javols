package net.javols.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import net.javols.Data;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Optional;

import static net.javols.compiler.Constants.NONPRIVATE_ACCESS_MODIFIERS;

public final class Context {

  // the annotated class
  private final TypeElement sourceElement;

  // the class that will be generated
  private final ClassName generatedClass;

  // the abstract methods in the annotated class
  private final List<Parameter> parameters;

  private final List<Parameter> positionalParams;

  private final List<Parameter> options;

  // whether "--help" is a special token
  private final boolean helpParameterEnabled;

  // program name from attribute
  private final String programName;

  private final ClassName optionType;

  private Context(
      TypeElement sourceElement,
      ClassName generatedClass,
      List<Parameter> parameters,
      List<Parameter> positionalParams,
      List<Parameter> options,
      boolean helpParameterEnabled,
      String programName,
      ClassName optionType) {
    this.sourceElement = sourceElement;
    this.generatedClass = generatedClass;
    this.parameters = parameters;
    this.positionalParams = positionalParams;
    this.options = options;
    this.helpParameterEnabled = helpParameterEnabled;
    this.programName = programName;
    this.optionType = optionType;
  }

  static Context create(
      TypeElement sourceElement,
      ClassName generatedClass,
      ClassName optionType,
      List<Parameter> parameters,
      List<Parameter> positionalParams,
      List<Parameter> options) {
    Data annotation = sourceElement.getAnnotation(Data.class);
    boolean helpParameterEnabled = !annotation.helpDisabled();

    return new Context(
        sourceElement,
        generatedClass,
        parameters,
        positionalParams,
        options, helpParameterEnabled,
        programName(sourceElement),
        optionType);
  }

  private static String programName(TypeElement sourceType) {
    Data annotation = sourceType.getAnnotation(Data.class);
    if (!annotation.value().isEmpty()) {
      return annotation.value();
    }
    String simpleName = sourceType.getSimpleName().toString();
    return ParamName.create(simpleName).snake('-');
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

  public ClassName optionType() {
    return optionType;
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

  public Optional<ClassName> helpRequestedType() {
    return helpParameterEnabled ? Optional.of(generatedClass.nestedClass("HelpRequested")) : Optional.empty();
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

  public List<Parameter> positionalParams() {
    return positionalParams;
  }

  public List<Parameter> options() {
    return options;
  }

  public boolean isHelpParameterEnabled() {
    return helpParameterEnabled;
  }

  public String programName() {
    return programName;
  }
}
