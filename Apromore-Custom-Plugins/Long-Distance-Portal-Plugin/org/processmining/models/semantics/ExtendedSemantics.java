package org.processmining.models.semantics;

public interface ExtendedSemantics<S, T> extends Semantics<S, T> {

	ExecutionInformation executeTransition(T toExecute);

}
