package net.javol.examples;

import net.javol.Command;
import net.javol.Option;
import net.javol.Param;

@Command(helpDisabled = true)
abstract class HelplessArguments {

  @Param(1)
  abstract String required();

  @Option("help")
  abstract boolean help();
}
