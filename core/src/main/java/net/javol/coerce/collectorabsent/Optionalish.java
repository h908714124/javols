package net.javol.coerce.collectorabsent;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import net.javol.compiler.TypeTool;

import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;

class Optionalish {

  private static final List<OptionalPrimitive> OPTIONAL_PRIMITIVES = Arrays.asList(
      new OptionalPrimitive(OptionalInt.class, Integer.class),
      new OptionalPrimitive(OptionalLong.class, Long.class),
      new OptionalPrimitive(OptionalDouble.class, Double.class));

  private final TypeMirror liftedType;

  private final TypeMirror wrappedType;

  private final Function<ParameterSpec, CodeBlock> extract;

  private Optionalish(
      Function<ParameterSpec, CodeBlock> extract,
      TypeMirror liftedType, TypeMirror wrappedType) {
    this.extract = extract;
    this.wrappedType = wrappedType;
    this.liftedType = liftedType;
  }

  private static class OptionalPrimitive {

    final Class<?> specialClass;
    final Class<? extends Number> wrapped;

    OptionalPrimitive(Class<?> specialClass, Class<? extends Number> wrapped) {
      this.specialClass = specialClass;
      this.wrapped = wrapped;
    }

    Function<ParameterSpec, CodeBlock> extractExpr() {
      return p -> CodeBlock.of("$N.isPresent() ? $T.of($N.get()) : $T.empty()", p, specialClass, p, specialClass);
    }
  }

  static Optional<Optionalish> unwrap(TypeMirror type, TypeTool tool) {
    Optional<Optionalish> optionalPrimtive = getOptionalPrimitive(type, tool);
    if (optionalPrimtive.isPresent()) {
      return optionalPrimtive;
    }
    return tool.unwrap(Optional.class, type)
        .map(wrapped -> new Optionalish(p -> CodeBlock.of("$N", p), type, wrapped));
  }

  private static Optional<Optionalish> getOptionalPrimitive(TypeMirror type, TypeTool tool) {
    for (OptionalPrimitive e : OPTIONAL_PRIMITIVES) {
      if (tool.isSameType(type, e.specialClass)) {
        return Optional.of(new Optionalish(
            e.extractExpr(),
            tool.optionalOf(e.wrapped),
            tool.asType(e.wrapped)));
      }
    }
    return Optional.empty();
  }

  /**
   * <ul>
   *   <li>{@code OptionalInt} -&gt; {@code Optional<Integer>}</li>
   *   <li>{@code Optional<Integer>} -&gt; {@code Optional<Integer>}</li>
   * </ul>
   *
   * @return lifted type
   */
  TypeMirror liftedType() {
    return liftedType;
  }

  /**
   * The function creates an expression of the original type.
   *
   * @param constructorParam the constructor constructorParam
   * @return extract expr
   */
  CodeBlock extractExpr(ParameterSpec constructorParam) {
    return extract.apply(constructorParam);
  }

  /**
   * <ul>
   *   <li>{@code OptionalInt} -&gt; {@code Integer}</li>
   *   <li>{@code Optional<Integer>} -&gt; {@code Integer}</li>
   * </ul>
   *
   * @return wrapped type
   */
  TypeMirror wrappedType() {
    return wrappedType;
  }
}
