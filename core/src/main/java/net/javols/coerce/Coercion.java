package net.javols.coerce;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;

public class Coercion {

  private final ParameterSpec constructorParam;

  private final CodeBlock mapExpr;

  private final CodeBlock extractExpr;

  private final Skew skew;

  public Coercion(CodeBlock mapExpr,
                  CodeBlock extractExpr,
                  Skew skew,
                  ParameterSpec constructorParam) {
    this.constructorParam = constructorParam;
    this.mapExpr = mapExpr;
    this.extractExpr = extractExpr;
    this.skew = skew;
  }

  public ParameterSpec constructorParam() {
    return constructorParam;
  }

  public CodeBlock mapExpr() {
    return mapExpr;
  }

  public CodeBlock extractExpr() {
    return extractExpr;
  }

  public Skew getSkew() {
    return skew;
  }
}
