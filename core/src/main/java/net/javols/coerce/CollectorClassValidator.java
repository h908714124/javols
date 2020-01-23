package net.javols.coerce;

import net.javols.coerce.reference.ReferenceTool;
import net.javols.coerce.reference.ReferencedType;
import net.javols.compiler.TypeTool;
import net.javols.compiler.TypevarMapping;
import net.javols.compiler.ValidationException;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collector;

import static net.javols.coerce.SuppliedClassValidator.commonChecks;
import static net.javols.coerce.Util.checkNotAbstract;
import static net.javols.coerce.reference.ExpectedType.COLLECTOR;

class CollectorClassValidator {

  private final Function<String, ValidationException> errorHandler;
  private final TypeTool tool;
  private final TypeElement collectorClass;
  private final TypeMirror originalReturnType;

  CollectorClassValidator(Function<String, ValidationException> errorHandler, TypeTool tool, TypeElement collectorClass, TypeMirror originalReturnType) {
    this.errorHandler = errorHandler;
    this.tool = tool;
    this.collectorClass = collectorClass;
    this.originalReturnType = originalReturnType;
  }

  CollectorInfo getCollectorInfo() {
    commonChecks(collectorClass);
    checkNotAbstract(collectorClass);
    ReferencedType<Collector> collectorType = new ReferenceTool<>(COLLECTOR, errorHandler, tool, collectorClass)
        .getReferencedType();
    TypeMirror inputType = collectorType.typeArguments().get(0);
    TypeMirror outputType = collectorType.typeArguments().get(2);
    TypevarMapping rightSolution = tool.unify(originalReturnType, outputType)
        .orElseThrow(this::boom);
    TypevarMapping leftSolution = new TypevarMapping(Collections.emptyMap(), tool);
    FlattenerResult result = new Flattener(tool, collectorClass)
        .mergeSolutions(leftSolution, rightSolution)
        .orElseThrow(this::boom);
    return CollectorInfo.create(tool, result.substitute(inputType).orElseThrow(f -> boom(f.getMessage())),
        collectorClass, collectorType.isSupplier(), result.getTypeParameters());
  }

  private ValidationException boom(String message) {
    return errorHandler.apply(COLLECTOR.boom(message));
  }
}
