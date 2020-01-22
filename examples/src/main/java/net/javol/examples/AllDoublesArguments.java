package net.javol.examples;

import net.javol.Command;
import net.javol.Option;
import net.javol.Param;

import java.util.List;
import java.util.Optional;

@Command
abstract class AllDoublesArguments {

  @Param(1)
  abstract List<Double> positional();

  @Option(value = "i", mnemonic = 'i')
  abstract List<Double> listOfDoubles();

  @Option("opt")
  abstract Optional<Double> optionalDouble();

  @Option("obj")
  abstract Double doubleObject();

  @Option("prim")
  abstract double primitiveDouble();
}
