package net.javol.examples;

import net.javol.Command;
import net.javol.Option;

import java.util.Optional;

@Command
abstract class PsArguments {

  @Option(value = "all", mnemonic = 'a')
  abstract boolean all();

  /**
   * This is the description.
   */
  @Option(value = "width", mnemonic = 'w')
  abstract Optional<Integer> width();
}
