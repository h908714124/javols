package net.javols.examples;

import net.javols.examples.fixture.ParserTestFixture;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class GradleArgumentsFooTest {

  private ParserTestFixture<GradleArguments.Foo> f =
      ParserTestFixture.create(new GradleArguments_Foo_Parser());

  @Test
  void testParserForNestedClass() {
    f.assertThat("--bar=4").succeeds("bar", Optional.of(4));
  }

  @Test
  void testPrint() {
    f.assertPrintsHelp(
        "Usage: foo [options...]",
        "",
        "  --bar BAR",
        "");
  }
}
