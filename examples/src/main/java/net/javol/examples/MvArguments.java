package net.javol.examples;

import net.javol.Command;
import net.javol.Param;

@Command
abstract class MvArguments {

  @Param(1)
  abstract String source();

  @Param(2)
  abstract String dest();
}
