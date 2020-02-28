/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.common;


import java.util.Comparator;

import org.zkoss.zul.Label;
import org.zkoss.zul.Row;

public class ProcessRankingColComparator implements Comparator {
	private boolean _asc;
	public ProcessRankingColComparator (boolean asc) {
		_asc = asc;
	}
	public int compare(Object o1, Object o2) {

		//o1 and o2 are rows to be compared according to their ranking value (9th children)
		Row r1 = (Row) o1, 
		r2 = (Row) o2;

		Label l1 = (Label) r1.getChildren().get(8),
		l2 = (Label) r2.getChildren().get(8);

		String s1 = l1.getValue(), s2 = l2.getValue();
		if (s1.length()==0 && s2.length()==0) {
			return _asc ? 1: 1;
		} else if (s1.length()!=0 && s2.length()==0) {
			return _asc ? 1: -1;
		} else if (s1.length()==0 && s2.length()!=0) {
			return _asc ? -1: 1;
		} else {
			Float i1 = Float.parseFloat(s1),
			i2 = Float.parseFloat(s2);
			int v = i1.compareTo(i2) ;
			return _asc ? v: -v;
		}
	}

}
