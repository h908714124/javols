package net.javol.examples;

import net.javol.Command;
import net.javol.Option;
import net.javol.Param;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Command
abstract class VariousArguments {

  @Option("bigDecimal")
  abstract BigDecimal bigDecimal();

  @Option("bigDecimalList")
  abstract List<BigDecimal> bigDecimalList();

  @Option("bigDecimalOpt")
  abstract Optional<BigDecimal> bigDecimalOpt();

  @Param(1)
  abstract Optional<BigDecimal> bigDecimalPos();

  @Option("bigInteger")
  abstract BigInteger bigInteger();

  @Option("bigIntegerList")
  abstract List<BigInteger> bigIntegerList();

  @Option("bigIntegerOpt")
  abstract Optional<BigInteger> bigIntegerOpt();

  @Param(2)
  abstract Optional<BigInteger> bigIntegerPos();

  @Option("fileList")
  abstract List<File> fileList();

  @Option("fileOpt")
  abstract Optional<File> fileOpt();

  @Param(3)
  abstract Optional<File> filePos();

  @Option("path")
  abstract Path path();

  @Option("pathList")
  abstract List<Path> pathList();

  @Option("pathOpt")
  abstract Optional<Path> pathOpt();

  @Param(4)
  abstract Optional<Path> pathPos();

  @Option("localDate")
  abstract LocalDate localDate();

  @Option("localDateList")
  abstract List<LocalDate> localDateList();

  @Option("localDateOpt")
  abstract Optional<LocalDate> localDateOpt();

  @Param(5)
  abstract Optional<LocalDate> localDatePos();

  @Option("uri")
  abstract URI uri();

  @Option("uriList")
  abstract List<URI> uriList();

  @Option("uriOpt")
  abstract Optional<URI> uriOpt();

  @Param(8)
  abstract Optional<URI> uriPos();

  @Option("pattern")
  abstract Pattern pattern();

  @Option("patternList")
  abstract List<Pattern> patternList();

  @Option("patternOpt")
  abstract Optional<Pattern> patternOpt();

  @Param(10)
  abstract Optional<Pattern> patternPos();
}
