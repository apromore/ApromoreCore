package org.apromore.canoniser.adapters.canonical2pnml;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RemoveDuplicateListItems {
	List<String> list = new LinkedList<String>();

	public List<String> transform(List<String> list) {
		this.list = list;
		Collections.sort(list);
		String lastValue = null;
		for (Iterator<String> i = list.iterator(); i.hasNext();) {
			String currentValue = i.next();
			if (lastValue != null && currentValue.equals(lastValue)) {

			} else {
				i.remove();
			}
			lastValue = currentValue;
		}
		return list;
	}

}
