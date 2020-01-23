package net.javols.examples;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionalIntegerArgumentsTest {

  @Test
  void testPresent() {
    OptionalIntegerArguments args = new OptionalIntegerArguments_Parser().parseOrExit(new String[]{"-a", "1"});
    assertEquals(Optional.of(1), args.a());
  }

  @Test
  void testAbsent() {
    OptionalIntegerArguments_Parser.ParseResult result = new OptionalIntegerArguments_Parser().parse(new String[]{});
    assertTrue(result instanceof OptionalIntegerArguments_Parser.ParsingFailed);
  }
}