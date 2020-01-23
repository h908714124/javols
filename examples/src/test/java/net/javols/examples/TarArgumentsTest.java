package net.javols.examples;

import net.javols.examples.fixture.ParserTestFixture;
import org.junit.jupiter.api.Test;

class TarArgumentsTest {

  private ParserTestFixture<TarArguments> f =
      ParserTestFixture.create(new TarArguments_Parser());

  @Test
  void testExtract() {
    f.assertThat("-x", "-f", "foo.tar").succeeds(
        "extract", true,
        "create", false,
        "verbose", false,
        "compress", false,
        "file", "foo.tar");
    f.assertThat("-v", "-x", "-f", "foo.tar").succeeds(
        "extract", true,
        "create", false,
        "verbose", true,
        "compress", false,
        "file", "foo.tar");
  }

  @Test
  void flagWithArgument() {
    f.assertThat("-xf").failsWithMessage("Invalid token: -xf");
    f.assertThat("--x=f").failsWithMessage("Invalid token: --x=f");
  }

  @Test
  void testPrint() {
    f.assertPrintsHelp(
        "Usage: tar-arguments [options...] -f <file>",
        "",
        "  -x, --x",
        "  -c, --c",
        "  -v, --v",
        "  -z, --z",
        "  -f, --f FILE",
        "");
  }
}
