package net.javol.coerce;

import net.javol.coerce.either.Either;
import net.javol.coerce.either.Left;
import net.javol.coerce.either.Right;
import net.javol.compiler.TypeTool;
import net.javol.compiler.TypevarMapping;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static net.javol.coerce.either.Either.left;
import static net.javol.coerce.either.Either.right;

public class Flattener {

  private final TypeTool tool;
  private final TypeElement targetElement;

  public Flattener(TypeTool tool, TypeElement targetElement) {
    this.tool = tool;
    this.targetElement = targetElement;
  }

  /**
   * @param leftSolution a mapping
   * @param rightSolution a mapping
   * @return type parameters in the correct order for {@code targetElement}
   */
  public Either<String, FlattenerResult> mergeSolutions(TypevarMapping leftSolution, TypevarMapping rightSolution) {
    return leftSolution.merge(rightSolution).flatMap(Function.identity(),
        this::getTypeParameters);
  }

  private Either<String, FlattenerResult> getTypeParameters(TypevarMapping solution) {
    List<TypeMirror> result = new ArrayList<>();
    Map<String, TypeMirror> mapping = new LinkedHashMap<>(solution.getMapping());
    List<? extends TypeParameterElement> parameters = targetElement.getTypeParameters();
    for (TypeParameterElement p : parameters) {
      List<? extends TypeMirror> bounds = p.getBounds();
      TypeMirror m = solution.get(p.toString());
      if (m == null || m.getKind() == TypeKind.TYPEVAR) {
        Either<String, TypeMirror> inferred = tool.getBound(p);
        if (inferred instanceof Left) {
          return left(((Left<String, TypeMirror>) inferred).value());
        }
        m = ((Right<String, TypeMirror>) inferred).value();
      }
      if (tool.isOutOfBounds(m, bounds)) {
        return left("Invalid bounds: Can't resolve " + p.toString() + " to " + m);
      }
      mapping.put(p.toString(), m);
      result.add(m);
    }
    return right(new FlattenerResult(result, new TypevarMapping(mapping, tool)));
  }
}
