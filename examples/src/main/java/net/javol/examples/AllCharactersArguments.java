package net.javol.examples;

import net.javol.Command;
import net.javol.Option;

import java.util.List;
import java.util.Optional;

@Command
abstract class AllCharactersArguments {

  @Option("smallChar")
  abstract char smallChar();

  @Option("bigChar")
  abstract Character bigChar();

  @Option("charOpt")
  abstract Optional<Character> charOpt();

  @Option("charList")
  abstract List<Character> charList();
}
