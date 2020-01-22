package net.javol.examples;

import net.javol.Command;
import net.javol.Option;
import net.javol.Param;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Command
abstract class CustomMapperArguments {

  /**
   * The mapper must be a Function from String to whatever-this-returns.
   * It must also have a package-visible no-arg constructor.
   */
  @Option(value = "date", mappedBy = DateMapper.class)
  abstract Date date();

  @Option(value = "optDate", mappedBy = DateMapper.class)
  abstract Optional<Date> optDate();

  @Option(value = "dateList", mappedBy = DateMapper.class)
  abstract List<Date> dateList();

  @Option(value = "verbosity", mappedBy = CustomBigIntegerMapperSupplier.class)
  abstract Optional<BigInteger> verbosity();

  @Option(value = "aRequiredInt", mappedBy = PositiveNumberMapper.class)
  abstract int aRequiredInt();

  @Option(value = "stringArray", mappedBy = ArrayMapper.class)
  abstract Optional<String[]> stringArray();

  @Option(value = "integerList", mappedBy = IntegerListMapper.class)
  abstract Optional<List<Integer>> integerList();

  @Option(value = "enumSet", mappedBy = EnumSetMapper.class)
  abstract Optional<Set<MyEnum>> enumSet();

  @Param(value = 1, mappedBy = BooleanMapper.class)
  abstract List<Boolean> booleanList();

  @Option(value = "optionalInts", mappedBy = OptionalIntMapper.class)
  abstract List<OptionalInt> optionalInts();

  @Option(value = "listWrapper", mappedBy = ListWrapperMapper.class)
  abstract Optional<List<String>> listWrapper();

  @Option(value = "notFlag", mappedBy = BooleanMapper.class)
  abstract Boolean notFlag();

  static class DateMapper implements Supplier<Function<String, Date>> {

    @Override
    public Function<String, Date> get() {
      return s -> new Date(Long.parseLong(s));
    }
  }

  static class PositiveNumberMapper implements Supplier<Function<String, Integer>> {

    @Override
    public Function<String, Integer> get() {
      return s -> {
        Integer i = Integer.valueOf(s);
        if (i < 0) {
          throw new IllegalArgumentException("The value cannot be negative.");
        }
        return i;
      };
    }
  }

  static class ArrayMapper implements Supplier<Function<String, String[]>> {

    @Override
    public Function<String, String[]> get() {
      return s -> new String[]{s};
    }
  }

  static class IntegerListMapper implements Supplier<Function<String, List<Integer>>> {

    @Override
    public Function<String, List<Integer>> get() {
      return s -> Arrays.stream(s.split(",", -1))
          .map(Integer::valueOf)
          .collect(Collectors.toList());
    }
  }

  static class EnumSetMapper implements Supplier<Function<String, Set<MyEnum>>> {

    @Override
    public Function<String, Set<MyEnum>> get() {
      return s -> Arrays.stream(s.split(",", -1))
          .map(MyEnum::valueOf)
          .collect(Collectors.toSet());
    }
  }

  static class BooleanMapper implements Supplier<Function<String, Boolean>> {

    @Override
    public Function<String, Boolean> get() {
      return Boolean::valueOf;
    }
  }

  interface ListWrapperMapperInterface<E> extends Supplier<Function<E, List<E>>> {
  }

  static class ListWrapperMapper implements ListWrapperMapperInterface<String> {

    @Override
    public Function<String, List<String>> get() {
      return Collections::singletonList;
    }
  }

  static class OptionalIntMapper implements Supplier<Function<String, OptionalInt>> {

    @Override
    public Function<String, OptionalInt> get() {
      return s -> {
        if (s.isEmpty()) {
          return OptionalInt.empty();
        }
        return OptionalInt.of(Integer.parseInt(s));
      };
    }
  }

  enum MyEnum {
    FOO, BAR
  }
}
