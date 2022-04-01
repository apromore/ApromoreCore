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
/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2009 Christian W. Guenther (christian@deckfour.org)
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.util;

import java.util.Collections;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import java.util.Set;

/**
 * Template implementation for a generic registry.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public abstract class XRegistry<T> {
	
	/**
	 * Registry set, holding all instances.
	 */
	private Set<T> registry;
	/**
	 * Holds the current default instance.
	 */
	private T current;
	
	/**
	 * Instantiates a new registry.
	 */
	public XRegistry() {
		registry = new UnifiedSet<T>();
		current = null;
	}
	
	/**
	 * Retrieves a set of all available instances.
	 */
	public Set<T> getAvailable() {
		return Collections.unmodifiableSet(registry);
	}
	
	/**
	 * Retrieves the current default instance.
	 */
	public T currentDefault() {
		return current;
	}
	
	/**
	 * Registers a new instance with this registry.
	 * 
	 * @param instance Instance to be registered.
	 */
	public void register(T instance) {
		if(!isContained(instance)) {
			registry.add(instance);
			if(current == null) {
				current = instance;
			}
		}
	}
	
	/**
	 * Sets the current default instance of
	 * this registry.
	 * 
	 * @param instance Instance to be the
	 * 	current default of this registry.
	 */
	public void setCurrentDefault(T instance) {
		registry.add(instance);
		current = instance;
	}
	
	/**
	 * Subclasses must implement this method. It is used
	 * by the registry to ensure that no duplicates are
	 * inserted.
	 * 
	 * @param a An instance a.
	 * @param b Another instance b.
	 * @return Whether the instances a and b are equivalent.
	 */
	protected abstract boolean areEqual(T a, T b);
	
	/**
	 * Checks whether the given instance is already
	 * contained in the registry.
	 * 
	 * @param instance Instance to check against registry.
	 * @return Whether the given instance is already registered.
	 */
	protected boolean isContained(T instance) {
		for(T ref : registry) {
			if(areEqual(instance, ref)) {
				return true;
			}
		}
		return false;
	}

}
