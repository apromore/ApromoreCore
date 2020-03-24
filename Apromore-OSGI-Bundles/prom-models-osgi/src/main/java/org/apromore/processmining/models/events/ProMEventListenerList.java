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