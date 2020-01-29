package net.javols.compiler;

import javax.lang.model.element.Modifier;
import java.util.EnumSet;
import java.util.Set;

import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;

public final class Constants {

  static final Set<Modifier> NONPRIVATE_ACCESS_MODIFIERS = EnumSet.of(PUBLIC, PROTECTED);
}
