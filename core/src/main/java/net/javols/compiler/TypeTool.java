package net.javols.compiler;

import net.javols.coerce.either.Either;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static net.javols.coerce.either.Either.left;
import static net.javols.coerce.either.Either.right;

public class TypeTool {

  private static final TypeVisitor<List<? extends TypeMirror>, Void> TYPEARGS =
      new SimpleTypeVisitor8<List<? extends TypeMirror>, Void>() {
        @Override
        public List<? extends TypeMirror> visitDeclared(DeclaredType declaredType, Void _null) {
          return declaredType.getTypeArguments();
        }

        @Override
        protected List<? extends TypeMirror> defaultAction(TypeMirror e, Void _null) {
          return Collections.emptyList();
        }
      };

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

  private static final TypeVisitor<Boolean, TypeTool> IS_JAVA_LANG_OBJECT = new SimpleTypeVisitor8<Boolean, TypeTool>() {
    @Override
    protected Boolean defaultAction(TypeMirror e, TypeTool tool) {
      return false;
    }

    @Override
    public Boolean visitDeclared(DeclaredType type, TypeTool tool) {
      TypeElement element = type.asElement().accept(TypeTool.AS_TYPE_ELEMENT, null);
      if (element == null) {
        return false;
      }
      return "java.lang.Object".equals(element.getQualifiedName().toString());
    }
  };

  private final Types types;

  private final Elements elements;

  // visible for testing
  public TypeTool(Elements elements, Types types) {
    this.types = types;
    this.elements = elements;
  }

  /**
   * @return {@code true} means failure
   */
  private String unify(TypeMirror x, TypeMirror y, Map<String, TypeMirror> acc) {
    if (y.getKind() == TypeKind.TYPEVAR) {
      acc.put(y.toString(), x);
      return null; // success
    }
    if (x.getKind() == TypeKind.TYPEVAR) {
      return "can't unify " + y + " with typevar " + x;
    }
    if (x.getKind() == TypeKind.DECLARED) {
      DeclaredType xx = asDeclared(x);
      if (xx.getTypeArguments().isEmpty()) {
        if (!isAssignable(y, x)) {
          return "Unification failed: can't assign " + y + " to " + x;
        }
      } else {
        if (!isSameErasure(x, y)) {
          return "Unification failed: " + y + " and " + x + " have different erasure";
        }
      }
    } else {
      if (!isSameErasure(x, y)) {
        return "Unification failed: " + y + " and " + x + " have different erasure";
      }
    }
    if (isRaw(x)) {
      return "raw type: " + x;
    }
    if (isRaw(y)) {
      return "raw type: " + y;
    }
    List<? extends TypeMirror> xargs = typeargs(x);
    List<? extends TypeMirror> yargs = typeargs(y);
    for (int i = 0; i < yargs.size(); i++) {
      String failure = unify(xargs.get(i), yargs.get(i), acc);
      if (failure != null) {
        return failure;
      }
    }
    return null; // success
  }

  public Either<String, TypevarMapping> unify(TypeMirror concreteType, TypeMirror ym) {
    Map<String, TypeMirror> acc = new LinkedHashMap<>();
    String failure = unify(concreteType, ym, acc);
    return failure != null ? left(failure) : right(new TypevarMapping(acc, this));
  }

  public boolean isRaw(TypeMirror m) {
    if (m.getKind() != TypeKind.DECLARED) {
      return false;
    }
    DeclaredType declaredType = asDeclared(m);
    TypeElement element = asTypeElement(m);
    return declaredType.getTypeArguments().isEmpty() && !element.getTypeParameters().isEmpty();
  }

  public DeclaredType getDeclaredType(Class<?> clazz, List<? extends TypeMirror> typeArguments) {
    return getDeclaredType(asTypeElement(clazz), typeArguments.toArray(new TypeMirror[0]));
  }

  public DeclaredType getDeclaredType(TypeElement element, TypeMirror[] typeArguments) {
    return types.getDeclaredType(element, typeArguments);
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

  public boolean isSameType(TypeMirror mirror, TypeMirror test) {
    return types.isSameType(mirror, test);
  }

  PrimitiveType getPrimitiveType(TypeKind kind) {
    return types.getPrimitiveType(kind);
  }

  public boolean isSameErasure(TypeMirror x, TypeMirror y) {
    if (x.getKind().isPrimitive()) {
      return isSameType(x, y);
    }
    return types.isSameType(types.erasure(x), types.erasure(y));
  }

  public boolean isAssignable(TypeMirror x, TypeMirror y) {
    return types.isAssignable(x, y);
  }

  public boolean isSameErasure(TypeMirror x, Class<?> y) {
    return isSameErasure(x, erasure(y));
  }

  public TypeMirror erasure(TypeMirror typeMirror) {
    return types.erasure(typeMirror);
  }

  public TypeMirror erasure(Class<?> type) {
    return erasure(asType(type));
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

  public List<? extends TypeMirror> getDirectSupertypes(TypeMirror mirror) {
    return types.directSupertypes(mirror);
  }

  public boolean isPrivateType(TypeMirror mirror) {
    Element element = types.asElement(mirror);
    if (element == null) {
      return false;
    }
    return element.getModifiers().contains(Modifier.PRIVATE);
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

  public boolean isOutOfBounds(TypeMirror mirror, List<? extends TypeMirror> bounds) {
    for (TypeMirror bound : bounds) {
      if (!types.isAssignable(mirror, bound)) {
        return true;
      }
    }
    return false;
  }

  public Either<String, TypeMirror> getBound(TypeParameterElement p) {
    List<? extends TypeMirror> bounds = p.getBounds();
    if (bounds.isEmpty()) {
      return right(getDeclaredType(Object.class, Collections.emptyList()));
    }
    if (bounds.size() >= 2) {
      return left("Intersection type is not supported for typevar " + p.toString());
    }
    return right(bounds.get(0));
  }

  public Either<Function<String, String>, TypeMirror> getSpecialization(TypeMirror thisType, TypeMirror thatType) {
    if (isAssignable(thisType, thatType)) {
      return right(thisType);
    }
    if (isAssignable(thatType, thisType)) {
      return right(thatType);
    }
    return left(key -> String.format("Cannot infer %s: %s vs %s", key, thisType, thatType));
  }

  private List<? extends TypeMirror> typeargs(TypeMirror mirror) {
    return mirror.accept(TYPEARGS, null);
  }

  public boolean isObject(TypeMirror mirror) {
    return mirror.accept(IS_JAVA_LANG_OBJECT, this);
  }
}
