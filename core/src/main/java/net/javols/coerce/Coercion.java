package net.javols.coerce;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import net.javols.compiler.ParamName;

public class Coercion {

  private final ParameterSpec constructorParam;
  private final FieldSpec field;
  private final ParamName paramName;

  private final CodeBlock mapExpr;

  private final CodeBlock extractExpr;
  private final CodeBlock collectExpr;

  private final Skew skew;

  public Coercion(BasicInfo basicInfo,
                  CodeBlock mapExpr,
                  CodeBlock collectExpr,
                  CodeBlock extractExpr,
                  Skew skew,
                  ParameterSpec constructorParam) {
    this.constructorParam = constructorParam;
    this.field = basicInfo.fieldSpec();
    this.paramName = basicInfo.parameterName();
    this.mapExpr = mapExpr;
    this.extractExpr = extractExpr;
    this.skew = skew;
    this.collectExpr = collectExpr;
  }

  public ParameterSpec constructorParam() {
    return constructorParam;
  }

  public FieldSpec field() {
    return field;
  }

  public ParamName paramName() {
    return paramName;
  }

  public CodeBlock mapExpr() {
    return mapExpr;
  }

  public CodeBlock collectExpr() {
    return collectExpr;
  }

  public CodeBlock extractExpr() {
    return extractExpr;
  }

  public Skew getSkew() {
    return skew;
  }

}
