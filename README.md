[![core](https://maven-badges.herokuapp.com/maven-central/com.github.h908714124/javols/badge.svg?style=plastic&subject=javols)](https://maven-badges.herokuapp.com/maven-central/com.github.h908714124/javols)
[![annotations](https://maven-badges.herokuapp.com/maven-central/com.github.h908714124/javols-annotations/badge.svg?color=red&style=plastic&subject=javols-annotations)](https://maven-badges.herokuapp.com/maven-central/com.github.h908714124/javols-annotations)

This annotation processor lets you define
mappers and required keys on key-value
structures with known `String` keys.

### Example

````java
@Data(String.class)
abstract class User {

  @Key("name")
  abstract String name();

  @Key("age")
  abstract int age();
}
````

This will generate code similar to the following:

````java
@Generated
class User_Parser {

  static User parse(Function<String, String> f) {
    return parse(f, key -> new IllegalArgumentException("Missing required key: <" + key + ">"));
  }

  static User parse(Function<String, String> f, Function<String, RuntimeException> errMissing) {
    return new UserImpl(
        Optional.ofNullable(f.apply("name")).map(nameMapper).orElseThrow(() -> 
                errMissing.apply("name")),
        Optional.ofNullable(f.apply("age")).map(numberMapper).orElseThrow(() -> 
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
Map<String, String> m = Map.of("name", "Heiko", "age", "26");
User user = User_Parser.create()
   .nameMapper(Function.identity())
   .ageMapper(Integer::parseInt) // throw runtime exception to signal parse error
   .prepare() // for when the abstract class has a constructor and fields
   .parse(m::get);

assertEquals("Heiko", user.name());
assertEquals(26, user.age());
````

### Skew rules

Whether a key is considered *required* or *optional* is determined by its type `K`, using these rules:

Key type                            | Skew
----------------------------------- | --------------------------------
`K` (exact match)                   | *required*
`Optional<K>`                       | *optional*
<code>Optional{Int&#124;Long&#124;Double}</code> | *optional*

### Running tests

````sh
./gradlew core:clean core:test examples:clean examples:test
````

### Example project

It's used in [mex-kit](https://github.com/h908714124/mex-kit/blob/master/src/main/java/com/example/Http.java)
(see also [here](https://github.com/h908714124/mex-kit/blob/master/src/main/java/com/example/Main.java)).
