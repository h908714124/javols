package net.javol.examples;

import net.javol.examples.fixture.ParserTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import static net.javol.examples.fixture.ParserTestFixture.assertArraysEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestArgumentsTest {

  private ParserTestFixture<RestArguments> f =
      ParserTestFixture.create(new RestArguments_Parser());

  private Map<String, String> messages = new HashMap<>();

  private String[] expected = {
      "Usage: rest-arguments [options...] <rest>...",
      "",
      "  rest         Hello yes",
      "  --file FILE  This is dog",
      ""
  };

  @BeforeEach
  void setup() {
    messages.put("file", "This\nis\ndog\n");
    messages.put("the.rest", "Hello\n   yes\n");
  }

  @Test
  void testBundleKey() {
    String[] help = f.getHelp(messages);
    assertArraysEquals(expected, help);
  }

  @Test
  void testBundleKeyFromResourceBundle() {
    ResourceBundle bundle = mock(ResourceBundle.class);
    when(bundle.getKeys()).thenReturn(new Vector<>(messages.keySet()).elements());
    messages.forEach((k, v) -> when(bundle.getString(eq(k))).thenReturn(v));
    String[] help = f.getHelp(bundle);
    assertArraysEquals(expected, help);
  }
}
