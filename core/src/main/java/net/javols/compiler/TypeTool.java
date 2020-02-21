package net.javols.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Optional;

public class TypeTool {

  public static final TypeVisitor<DeclaredType, Void> AS_DECLARED =
      new SimpleTypeVisitor8<DeclaredType, Void>() {
        @Override
        public DeclaredType visitDeclared(DeclaredType declaredType, Void _null) {
          return declaredType;
        }
      };

  private static final TypeVisitor<PrimitiveType, Void> AS_PRIMITIVE =
      new SimpleTypeVisitor8<PrimitiveType, Void>() {
        @Override
        public PrimitiveType visitPrimitive(PrimitiveType primitiveType, Void _null) {
          return primitiveType;
        }
      };

  public static final ElementVisitor<TypeElement, Void> AS_TYPE_ELEMENT =
      new SimpleElementVisitor8<TypeElement, Void>() {
        @Override
        public TypeElement visitType(TypeElement typeElement, Void _null) {
          return typeElement;
        }
      };

  private final Types types;

  private final Elements elements;

  // visible for testing
  public TypeTool(Elements elements, Types types) {
    this.types = types;
    this.elements = elements;
  }

  public boolean isReachable(TypeMirror mirror) {
    TypeKind kind = mirror.getKind();
    if (kind != TypeKind.DECLARED) {
      return true;
    }
    DeclaredType declared = asDeclared(mirror);
    if (declared.asElement().getModifiers().contains(Modifier.PRIVATE)) {
      return false;
    }
    List<? extends TypeMirror> typeArguments = declared.getTypeArguments();
    for (TypeMirror typeArgument : typeArguments) {
      if (!isReachable(typeArgument)) {
        return false;
      }
    }
    return true;
  }

  public boolean isSameType(TypeMirror mirror, Class<?> test) {
    return types.isSameType(mirror, asTypeElement(test).asType());
  }

  public Optional<TypeMirror> unwrap(Class<?> wrapper, TypeMirror mirror) {
    if (mirror.getKind() != TypeKind.DECLARED) {
      return Optional.empty();
    }
    if (!isSameErasure(mirror, wrapper)) {
      return Optional.empty();
    }
    DeclaredType declaredType = asDeclared(mirror);
    if (declaredType.getTypeArguments().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(declaredType.getTypeArguments().get(0));
  }

  private boolean isSameErasure(TypeMirror x, Class<?> y) {
    return types.isSameType(types.erasure(x), types.erasure(asType(y)));
  }

  public TypeMirror asType(Class<?> type) {
    return elements.getTypeElement(type.getCanonicalName()).asType();
  }

  public DeclaredType optionalOf(Class<?> type) {
    return optionalOf(asTypeElement(type).asType());
  }

  private DeclaredType optionalOf(TypeMirror typeMirror) {
    return types.getDeclaredType(asTypeElement(Optional.class), typeMirror);
  }

  public TypeMirror box(TypeMirror mirror) {
    PrimitiveType primitive = mirror.accept(AS_PRIMITIVE, null);
    if (primitive == null) {
      return mirror;
    }
    return types.boxedClass(primitive).asType();
  }

  private TypeElement asTypeElement(Class<?> clazz) {
    return elements.getTypeElement(clazz.getCanonicalName());
  }

  public TypeElement asTypeElement(TypeMirror mirror) {
    Element element = types.asElement(mirror);
    if (element == null) {
      throw new IllegalArgumentException("not an element: " + mirror);
    }
    return asTypeElement(element);
  }

  public static TypeElement asTypeElement(Element element) {
    TypeElement result = element.accept(AS_TYPE_ELEMENT, null);
    if (result == null) {
      throw new IllegalArgumentException("not a type element: " + element);
    }
    return result;
  }

  public static DeclaredType asDeclared(TypeMirror mirror) {
    DeclaredType result = mirror.accept(AS_DECLARED, null);
    if (result == null) {
      throw new IllegalArgumentException("not declared: " + mirror);
    }
    return result;
  }
}
