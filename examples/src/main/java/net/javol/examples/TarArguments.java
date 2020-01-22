package net.javol.examples;


import net.javol.Command;
import net.javol.Option;

@Command
abstract class TarArguments {

  @Option(value = "x", mnemonic = 'x')
  abstract boolean extract();

  @Option(value = "c", mnemonic = 'c')
  abstract boolean create();

  @Option(value = "v", mnemonic = 'v')
  abstract boolean verbose();

  @Option(value = "z", mnemonic = 'z')
  abstract boolean compress();

  @Option(value = "f", mnemonic = 'f')
  abstract String file();
}
