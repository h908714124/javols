package net.javol.examples;

import net.javol.Command;
import net.javol.Option;
import net.javol.Param;

import java.util.List;
import java.util.Optional;

@Command
abstract class AllFloatsArguments {

  @Param(1)
  abstract List<Float> positional();

  @Option(value = "i", mnemonic = 'i')
  abstract List<Float> listOfFloats();

  @Option("opt")
  abstract Optional<Float> optionalFloat();

  @Option("obj")
  abstract Float floatObject();

  @Option("prim")
  abstract float primitiveFloat();
}
