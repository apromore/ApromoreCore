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
package org.apromore.processmining.models.events;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

public class ProMEventListenerList<T extends EventListener> {

	private final List<WeakReference<T>> listeners = new ArrayList<WeakReference<T>>(2);

	public void add(T listener) {
		synchronized (listeners) {
			listeners.add(new WeakReference<T>(listener));
		}
	}

	public int getListenerCount() {
		return listeners.size();
	}

	protected List<T> getListeners() {
		List<T> result = new ArrayList<T>();
		Iterator<WeakReference<T>> it = listeners.iterator();
		while (it.hasNext()) {
			T object = it.next().get();
			if (object != null) {
				result.add(object);
			} else {
				it.remove();
			}
		}
		return result;
	}

	public void remove(T listener) {
		while (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	public String toString() {
		return listeners.toString();
	}

	public void removeAll() {
		listeners.clear();
	}

}
