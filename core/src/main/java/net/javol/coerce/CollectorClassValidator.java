package net.javol.coerce;

import net.javol.coerce.reference.ReferenceTool;
import net.javol.coerce.reference.ReferencedType;
import net.javol.compiler.TypeTool;
import net.javol.compiler.TypevarMapping;
import net.javol.compiler.ValidationException;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collector;

import static net.javol.coerce.SuppliedClassValidator.commonChecks;
import static net.javol.coerce.Util.checkNotAbstract;
import static net.javol.coerce.reference.ExpectedType.COLLECTOR;

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
