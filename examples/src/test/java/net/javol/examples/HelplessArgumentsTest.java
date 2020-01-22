package net.javol.examples;

import net.javol.examples.fixture.ParserTestFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelplessArgumentsTest {

  private ParserTestFixture<HelplessArguments> f =
      ParserTestFixture.create(new HelplessArguments_Parser());

  @Test
  void success0() {
    HelplessArguments_Parser.ParseResult opt = new HelplessArguments_Parser().parse(new String[]{"x"});
    assertTrue(opt instanceof HelplessArguments_Parser.ParsingSuccess);
    HelplessArguments args = ((HelplessArguments_Parser.ParsingSuccess) opt).getResult();
    assertEquals("x", args.required());
    assertFalse(args.help());
  }

  @Test
  void success1() {
    HelplessArguments_Parser.ParseResult opt = new HelplessArguments_Parser().parse(new String[]{"x", "--help"});
    assertTrue(opt instanceof HelplessArguments_Parser.ParsingSuccess);
    HelplessArguments args = ((HelplessArguments_Parser.ParsingSuccess) opt).getResult();
    assertTrue(args.help());
    assertEquals("x", args.required());
  }

  @Test
  void success2() {
    HelplessArguments_Parser.ParseResult opt = new HelplessArguments_Parser().parse(new String[]{"--help", "x"});
    assertTrue(opt instanceof HelplessArguments_Parser.ParsingSuccess);
    HelplessArguments args = ((HelplessArguments_Parser.ParsingSuccess) opt).getResult();
    assertTrue(args.help());
    assertEquals("x", args.required());
  }

  @Test
  void errorNoArguments() {
    f.assertThat().failsWithMessage("Missing required: REQUIRED");
  }

  @Test
  void errorInvalidOption() {
    f.assertThat("-p").failsWithMessage("Invalid option: -p");
  }
}
