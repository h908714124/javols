package net.javol.examples;

import net.javol.Command;
import net.javol.Param;

import java.util.List;

@Command
abstract class GitArguments {

  @Param(1)
  abstract String command();

  @Param(2)
  abstract List<String> remainingArgs();
}
