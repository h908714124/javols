package net.javol.examples;

import net.javol.Command;
import net.javol.Option;
import net.javol.Param;

import java.util.List;
import java.util.Optional;

/**
 * curl  is  a  tool  to  transfer data from or to a server
 * using one of the supported protocols.
 * <p>
 * curl offers a busload of useful tricks.
 * <p>
 * curl is powered by libcurl for all transfer-related features.
 * See libcurl(3) for details.
 */
@Command("curl")
abstract class CurlArguments {

  /**
   * Optional<String> for regular arguments
   */
  @Option(value = "request", mnemonic = 'X')
  abstract Optional<String> method();

  /**
   * List<String> for repeatable arguments
   */
  @Option(value = "H", mnemonic = 'H')
  abstract List<String> headers();

  /**
   * boolean for flags
   */
  @Option(value = "verbose", mnemonic = 'v')
  abstract boolean verbose();

  @Option(value = "include", mnemonic = 'i')
  abstract boolean include();

  @Param(1)
  abstract List<String> urls();
}
