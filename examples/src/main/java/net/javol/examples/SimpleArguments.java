package net.javol.examples;

import net.javol.Command;
import net.javol.Option;

import java.util.Optional;

@Command
abstract class SimpleArguments {

  @Option(value = "x", mnemonic = 'x')
  abstract boolean extract();

  @Option("file")
  abstract Optional<String> file();
}
