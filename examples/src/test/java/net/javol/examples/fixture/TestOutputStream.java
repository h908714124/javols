package net.javol.examples.fixture;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static net.javol.examples.fixture.ParserTestFixture.assertArraysEquals;

public class TestOutputStream {

  private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

  public final PrintStream out = new PrintStream(baos);

  @Override
  public String toString() {
    return new String(baos.toByteArray());
  }

  public void assertEquals(String... expected) {
    String stdout = baos.toString();
    String[] actual = stdout.split("\\r?\\n", -1);
    assertArraysEquals(expected, actual);
  }
}
