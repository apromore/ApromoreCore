package org.apromore.portal.common;

import java.util.Comparator;

import org.zkoss.zul.Row;
import org.zkoss.zul.Toolbarbutton;

public class ProcessNameColComparator implements Comparator {
	private boolean _asc;
	public ProcessNameColComparator (boolean asc) {
		_asc = asc;
	}
	public int compare(Object o1, Object o2) {
		
		//o1 and o2 are rows to be compared according to their 2nd children (toolbarbutton)
		Row r1 = (Row) o1, 
		    r2 = (Row) o2;
		Toolbarbutton tb1 = (Toolbarbutton) r1.getChildren().get(2),
		              tb2 = (Toolbarbutton) r2.getChildren().get(2);
		
		String s1 = tb1.getLabel(),
		       s2 = tb2.getLabel();
		
		int v = s1.compareTo(s2);
		return _asc ? v: -v;
	}
}
