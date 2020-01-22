package net.javol.examples;

import net.javol.Command;
import net.javol.Option;
import net.javol.Param;

import java.util.List;

@Command
abstract class RestArguments {

  /**
   * This is the file.
   */
  @Option("file")
  abstract List<String> file();

  @Param(value = 1, bundleKey = "the.rest")
  abstract List<String> rest();
}
