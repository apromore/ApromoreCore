package org.apromore.portal.common;

import java.util.Comparator;

import org.zkoss.zul.Label;
import org.zkoss.zul.Row;

public class ProcessIdColComparator implements Comparator {
	private boolean _asc;
	public ProcessIdColComparator (boolean asc) {
		_asc = asc;
	}
	public int compare(Object o1, Object o2) {
		
		//o1 and o2 are rows to be compared according to their 2nd children (toolbarbutton)
		Row r1 = (Row) o1, 
		    r2 = (Row) o2;
			
		Label l1 = (Label) r1.getChildren().get(2),
			l2 = (Label) r2.getChildren().get(2);
		
		Integer i1 = Integer.parseInt(l1.getValue()),
				i2 = Integer.parseInt(l2.getValue());
		
		int v = i1.compareTo(i2) ;
		return _asc ? v: -v;
	}
}
