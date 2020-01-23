package net.javols.examples;

import net.javols.examples.fixture.ParserTestFixture;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

class AllFloatsArgumentsTest {

  private ParserTestFixture<AllFloatsArguments> f =
      ParserTestFixture.create(new AllFloatsArguments_Parser());

  @Test
  void listOfInteger() {
    f.assertThat("-i1.5", "-i2.5", "-i2.5", "-i3.5", "--obj=1.5", "--prim=1.5").succeeds(
        "positional", emptyList(),
        "listOfFloats", asList(1.5f, 2.5f, 2.5f, 3.5f),
        "optionalFloat", Optional.empty(),
        "floatObject", 1.5f,
        "primitiveFloat", 1.5f);
  }

  @Test
  void optionalInteger() {
    f.assertThat("--opt", "1.5", "--obj=1.5", "--prim=1.5").succeeds(
        "positional", emptyList(),
        "listOfFloats", emptyList(),
        "optionalFloat", Optional.of(1.5f),
        "floatObject", 1.5f,
        "primitiveFloat", 1.5f);
  }

  @Test
  void positional() {
    f.assertThat("--obj=1.5", "--prim=1.5", "5.5", "3.5").succeeds(
        "positional", asList(5.5f, 3.5f),
        "listOfFloats", emptyList(),
        "optionalFloat", Optional.empty(),
        "floatObject", 1.5f,
        "primitiveFloat", 1.5f);
  }

}