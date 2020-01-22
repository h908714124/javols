package net.javol.examples;

import net.javol.Command;
import net.javol.Param;

@Command
abstract class ExtremelySimpleArguments {

  @Param(value = 1)
  abstract int hello();
}
