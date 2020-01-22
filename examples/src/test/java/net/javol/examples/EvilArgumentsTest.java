package net.javol.examples;

import net.javol.examples.fixture.ParserTestFixture;
import org.junit.jupiter.api.Test;

class EvilArgumentsTest {

  private ParserTestFixture<EvilArguments> f =
      ParserTestFixture.create(new EvilArguments_Parser());

  @Test
  void basicTest() {
    f.assertThat("--fancy=1", "--fAncy=2", "--f_ancy=3", "--f__ancy=3", "--blub=4", "--Blub=5").succeeds(
        "fancy", "1",
        "fAncy", "2",
        "f_ancy", "3",
        "f__ancy", "3",
        "blub", "4",
        "Blub", "5");
  }

  @Test
  void testPrint() {
    f.assertPrintsHelp(
        "Usage: evil-arguments --fancy <fancy> --fAncy <f_ancy> --f_ancy <f_ancy_2>",
        "        --f__ancy <f_ancy_3> --blub <blub> --Blub <blub_5>",
        "",
        "  --fancy FANCY",
        "  --fAncy F_ANCY",
        "  --f_ancy F_ANCY_2",
        "  --f__ancy F_ANCY_3",
        "  --blub BLUB",
        "  --Blub BLUB_5",
        "");
  }
}
