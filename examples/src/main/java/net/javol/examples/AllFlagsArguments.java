package net.javol.examples;

import net.javol.Command;
import net.javol.Option;

@Command
abstract class AllFlagsArguments {

  @Option("smallFlag")
  abstract boolean smallFlag();

  @Option("bigFlag")
  abstract Boolean bigFlag();
}
