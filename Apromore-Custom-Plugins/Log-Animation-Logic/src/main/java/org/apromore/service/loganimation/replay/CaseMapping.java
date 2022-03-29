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
package org.apromore.service.loganimation.replay;

import java.util.Collection;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;

/**
 * Mapping from case IDs to indexes and vice versa.
 * @author Bruce Nguyen
 *
 */
public class CaseMapping {
	private HashBiMap<String, Integer> mapping = new HashBiMap<>();
	
	public CaseMapping(Collection<XTrace> traces) {
		int index=0;
		for (XTrace trace : traces) {
			mapping.put(XConceptExtension.instance().extractName(trace), index);
			index++;
		}
	}
	
	public int getIndex(String caseId) {
		return mapping.getIfAbsent(caseId, () -> -1);
	}
	
	public String getId(int index) {
		return mapping.inverse().getIfAbsent(index, () -> "");
	}
	
	public int size() {
		return mapping.size();
	}
}
