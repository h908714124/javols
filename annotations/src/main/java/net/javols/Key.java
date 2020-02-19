package net.javols;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for key methods.
 * The annotated method must be abstract
 * and have an empty argument list.
 *
 * @see <a href="https://github.com/h908714124/javols">github</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Key {

  /**
   * The name literal of this key.
   *
   * @return a string that is unique among the key methods
   */
  String value();
}
