package net.javol.coerce;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;

public class NonFlagCoercion extends Coercion {

  private final CodeBlock collectExpr;

  private final CodeBlock mapExpr;

  private final CodeBlock extractExpr;

  private final Skew skew;

  public NonFlagCoercion(
      BasicInfo basicInfo,
      CodeBlock collectExpr,
      CodeBlock mapExpr,
      CodeBlock extractExpr,
      NonFlagSkew skew,
      ParameterSpec constructorParam) {
    super(constructorParam, basicInfo.fieldSpec(), basicInfo.parameterName());
    this.collectExpr = collectExpr;
    this.mapExpr = mapExpr;
    this.extractExpr = extractExpr;
    this.skew = skew.widen();
  }

  public CodeBlock mapExpr() {
    return mapExpr;
  }

  public CodeBlock extractExpr() {
    return extractExpr;
  }

  public CodeBlock collectExpr() {
    return collectExpr;
  }

  public Skew getSkew() {
    return skew;
  }
}
