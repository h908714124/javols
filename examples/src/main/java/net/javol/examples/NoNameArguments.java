package net.javol.examples;


import net.javol.Command;
import net.javol.Option;

import java.util.List;
import java.util.Optional;

@Command
abstract class NoNameArguments {

  @Option("message")
  abstract Optional<String> message();

  @Option("file")
  abstract List<String> file();

  @Option(mnemonic = 'v', value = "verbosity")
  abstract Optional<Integer> verbosity();

  @Option(mnemonic = 'n', value = "number")
  abstract int number();

  @Option("cmos")
  abstract boolean cmos();
}
