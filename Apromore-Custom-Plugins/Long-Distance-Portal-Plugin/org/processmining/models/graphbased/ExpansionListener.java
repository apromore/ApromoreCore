package org.processmining.models.graphbased;

import java.util.EventListener;

import org.processmining.framework.plugin.events.ProMEventListenerList;

public interface ExpansionListener extends EventListener {

	void nodeExpanded(Expandable source);

	void nodeCollapsed(Expandable source);

	public class ListenerList extends ProMEventListenerList<ExpansionListener> {
		public void fireNodeExpanded(Expandable source) {
			for (ExpansionListener listener : getListeners()) {
				listener.nodeExpanded(source);
			}
		}

		public void fireNodeCollapsed(Expandable source) {
			for (ExpansionListener listener : getListeners()) {
				listener.nodeCollapsed(source);
			}
		}

	}

}
