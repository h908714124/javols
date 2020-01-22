package net.javol.examples;


import net.javol.Command;
import net.javol.Option;
import net.javol.Param;

import java.util.List;

@Command
abstract class RequiredArguments {

  @Option("dir")
  abstract String dir();

  @Param(1)
  abstract List<String> otherTokens();
}
