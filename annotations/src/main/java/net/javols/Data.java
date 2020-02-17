package net.javols;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks an abstract model class
 * that contains &quot;key methods&quot;, which correspond
 * to known keys of a map-like structure.
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
   * The type of the values of the target map.
   * For example, if the target map is a {@code Map<String, Object>},
   * then this would be {@code Object.class}.
   *
   * @return the value class
   */
  Class<?> valueClass() default String.class;

  /**
   * Declares a transform function that will be applied for each key.
   * The output of this function will be passed to the mappers,
   * see {@link Key#mappedBy()}.
   *
   * @return an optional transform function (or function-supplier)
   */
  Class<?> transform() default Object.class;
}
