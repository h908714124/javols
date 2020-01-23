package net.javols.examples;

import net.javols.examples.fixture.ParserTestFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtremelySimpleArgumentsTest {

  private ParserTestFixture<ExtremelySimpleArguments> f =
      ParserTestFixture.create(new ExtremelySimpleArguments_Parser());

  @Test
  void simpleTest() {
    assertEquals(1, f.parse("1").hello());
  }
}
