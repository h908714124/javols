package net.javols.examples;

import net.javols.examples.fixture.ParserTestFixture;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

class AllLongsArgumentsTest {

  private ParserTestFixture<AllLongsArguments> f =
      ParserTestFixture.create(new AllLongsArguments_Parser());

  @Test
  void listOfInteger() {
    f.assertThat("-i1", "-i2", "-i2", "-i3", "--obj=1", "--prim=1").succeeds(
        "positional", emptyList(),
        "listOfLongs", asList(1L, 2L, 2L, 3L),
        "optionalLong", Optional.empty(),
        "longObject", 1L,
        "primitiveLong", 1L);
  }

  @Test
  void optionalInteger() {
    f.assertThat("--opt", "1", "--obj=1", "--prim=1").succeeds(
        "positional", emptyList(),
        "listOfLongs", emptyList(),
        "optionalLong", Optional.of(1L),
        "longObject", 1L,
        "primitiveLong", 1L);
  }

  @Test
  void positional() {
    f.assertThat("--obj=1", "--prim=1", "5", "3").succeeds(
        "positional", asList(5L, 3L),
        "listOfLongs", emptyList(),
        "optionalLong", Optional.empty(),
        "longObject", 1L,
        "primitiveLong", 1L);
  }
}
