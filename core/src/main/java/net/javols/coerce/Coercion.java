package net.javols.coerce;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import net.javols.coerce.mapper.MapperGap;

public class Coercion {

  private final ParameterSpec constructorParam;
  private final MapperGap gap;
  private final CodeBlock extractExpr;
  private final Skew skew;

  public Coercion(MapperGap gap,
                  CodeBlock extractExpr,
                  Skew skew,
                  ParameterSpec constructorParam) {
    this.constructorParam = constructorParam;
    this.gap = gap;
    this.extractExpr = extractExpr;
    this.skew = skew;
  }

  public ParameterSpec constructorParam() {
    return constructorParam;
  }

  public MapperGap gap() {
    return gap;
  }

  public CodeBlock extractExpr() {
    return extractExpr;
  }

  public Skew getSkew() {
    return skew;
  }
}
