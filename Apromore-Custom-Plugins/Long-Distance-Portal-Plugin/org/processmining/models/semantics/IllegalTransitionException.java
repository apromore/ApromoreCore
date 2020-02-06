package org.processmining.models.semantics;

public class IllegalTransitionException extends Exception {

	private static final long serialVersionUID = -3136219267846046893L;

	public IllegalTransitionException(Object trans, Object state) {
		super("Cannot execute transition " + trans + " in state " + state);
	}

	public <S> IllegalTransitionException(Object trans, Object state, String reason) {
		super("Cannot execute transition " + trans + " in state " + state + " Reason: " + reason);
	}
}
