[![core](https://maven-badges.herokuapp.com/maven-central/com.github.h908714124/javols/badge.svg?style=plastic&subject=javols)](https://maven-badges.herokuapp.com/maven-central/com.github.h908714124/javols)
[![annotations](https://maven-badges.herokuapp.com/maven-central/com.github.h908714124/javols-annotations/badge.svg?color=red&style=plastic&subject=javols-annotations)](https://maven-badges.herokuapp.com/maven-central/com.github.h908714124/javols-annotations)

This annotation processor lets you define
mappers and required keys on key-value
structures, such as
[Map](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html) and
[Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html).

### Example

````java
@Data
abstract class User {

  @Key("name")
  abstract String name();

  @Key(value = "age", mappedBy = NumberMapper.class)
  abstract int age();

  static class NumberMapper implements Function<String, Integer> {
    public Integer apply(String s) {
      int result = Integer.parseInt(s);
      if (result < 0) throw new IllegalArgumentException("Invalid: " + s);
      return result;
    }
  }
}
````

This will generate the following parser:

````java
@Generated
class User_Parser {

  static User parse(Function<String, String> f) {
    return parse(f, key -> new IllegalArgumentException("Missing required key: <" + key + ">"));
  }

  static User parse(Function<String, String> f, Function<String, RuntimeException> errMissing) {
    return new UserImpl(
        Optional.ofNullable(f.apply("name")).map(Function.identity()).orElseThrow(() -> 
                errMissing.apply("name")),
        Optional.ofNullable(f.apply("age")).map(new User.NumberMapper()).orElseThrow(() -> 
                errMissing.apply("age")));
  }

  private static class UserImpl extends User {
    String name;
    int age;

    UserImpl(String name, int age) {
      this.name = name;
      this.age = age;
    }

    String name() { return name; }
    int age() { return age; }
  }
}
````

which can be used like this:

````java
Map<String, String> m = Map.of("name", "Hauke", "age", "26");
User user = User_Parser.parse(m::get);

assertEquals("Hauke", user.name());
assertEquals(26, user.age());
````

### Skew rules

Whether or not a key is considered optional is determined by its type.
These are the rules if no mapper is defined:

Parameter type                      | Skew
----------------------------------- | --------------------------------
`X` (exact match)                   | *required*
`Optional<X>`                       | *optional*
<code>Optional{Int&#124;Long&#124;Double}</code> | *optional*

where `X` is one of the "auto types" (basically primitives excluding `boolean`, plus `String`, `File` and `Path`).

With an explicit mapper, any type can be mapped (not just the auto types), and these rules apply:

Mapper return type      | Parameter type              | Skew
----------------------- | --------------------------- | ------------
`R`                     | `R` (exact match)           | *required*
`R`                     | `Optional<R>`               | *optional*
`Integer`               | `OptionalInt`               | *optional*
`Long`                  | `OptionalLong`              | *optional*
`Double`                | `OptionalDouble`            | *optional*

### Running tests

````sh
./gradlew core:clean core:test examples:clean examples:test
````

