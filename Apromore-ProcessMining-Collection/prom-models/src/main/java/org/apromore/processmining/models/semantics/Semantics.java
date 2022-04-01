/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.processmining.models.semantics;

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
