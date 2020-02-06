package org.processmining.models.semantics;

import java.io.Serializable;
import java.util.Collection;

public interface Semantics<S, T> extends Serializable {

	void setCurrentState(S currentState);

	S getCurrentState();

	Collection<T> getExecutableTransitions();

	ExecutionInformation executeExecutableTransition(T toExecute) throws IllegalTransitionException;

	/**
	 * Initializes this semantics. Note that the set of transitions is
	 * considered read only, i.e. no changes can be made to it by a
	 * Semantics<S,T> implementation. However, the initial state is not read
	 * only.
	 * 
	 * @param transitions
	 * @param initialState
	 */
	void initialize(Collection<T> transitions, S initialState);
}
