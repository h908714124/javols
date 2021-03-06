package net.javols;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks an abstract model class
 * that contains &quot;key methods&quot;, which correspond
 * to the known keys of the target map.
 *
 * Each of its abstract methods must be annotated with
 * the {@link Key} annotation.
 *
 * @see <a href="https://github.com/h908714124/javols">github</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Data {

  /**
   * The value type of the target map.
   *
   * @return a non-private class
   */
  Class<?> value();
}
