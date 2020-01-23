package net.javols.coerce;

import com.squareup.javapoet.CodeBlock;
import net.javols.compiler.ValidationException;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.lang.model.element.Modifier.ABSTRACT;

public final class Util {

  public static CodeBlock getTypeParameterList(List<TypeMirror> params) {
    if (params.isEmpty()) {
      return CodeBlock.builder().build();
    }
    return CodeBlock.of(Stream.generate(() -> "$T")
        .limit(params.size())
        .collect(Collectors.joining(", ", "<", ">")), params.toArray());
  }

  public static void checkNotAbstract(TypeElement typeElement) {
    if (typeElement.getModifiers().contains(ABSTRACT)) {
      throw ValidationException.create(typeElement, "The class may not be abstract.");
    }
  }

  public static String addBreaks(String code) {
    return code.replace(" ", "$W");
  }
}
