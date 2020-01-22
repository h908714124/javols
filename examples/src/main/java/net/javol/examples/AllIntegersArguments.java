package net.javol.examples;

import net.javol.Command;
import net.javol.Option;
import net.javol.Param;

import java.util.List;
import java.util.Optional;

@Command
abstract class AllIntegersArguments {

  @Param(1)
  abstract List<Integer> positional();

  @Option(value = "i", mnemonic = 'i')
  abstract List<Integer> listOfIntegers();

  @Option("opt")
  abstract Optional<Integer> optionalInteger();

  @Option("obj")
  abstract Integer integer();

  @Option("prim")
  abstract int primitiveInt();
}
