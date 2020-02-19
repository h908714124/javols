package net.javols;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks an abstract model class
 * that contains &quot;key methods&quot;, which correspond
 * to known keys of a {@link String}-keyed map.
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
   * Declares a transform function that will be applied for each key.
   * The input type of this function defines the value type of
   * the target map. The default transform is the identity on strings.
   *
   * @return an optional transform function, or function-supplier
   */
  Class<?> transform() default Object.class;
}
