/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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
package org.apromore.prodrift.mining.log.local;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.Pair;
import org.processmining.plugins.log.logabstraction.BasicLogRelations;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class AlphaRelations {
	
	public static final String LENGTH_ONE_SUFFIX = "__loop";
	private Multimap<String, String> parallelRelations;
	private Set<String> lengthOneLoops;

	public AlphaRelations(XLog log) {
		BasicLogRelations alphaRelations = new BasicLogRelations(log);
		parallelRelations = LinkedHashMultimap.create();
		lengthOneLoops = new HashSet<String>();
		
		// =======================================
		// COPY ALPHA RELATIONS
		// =======================================		
		// ---------------------------------------
		// -- LENGTH-ONE LOOP
		// ---------------------------------------
		for (XEventClass eclass: alphaRelations.getLengthOneLoops().keySet()) {
			Pair<String, String> p1 = split(eclass);
			if (p1.getSecond().equalsIgnoreCase("complete")) {
				String eventLabel = p1.getFirst();
				lengthOneLoops.add(eventLabel);
			}
		}
		// ---------------------------------------
		// -- CONCURRENCY
		// ---------------------------------------
		for (Pair<XEventClass, XEventClass> pair: alphaRelations.getParallelRelations().keySet()) {
			// Avoid SELF-CONCURRENCY relations
			if (pair.getFirst().equals(pair.getSecond())) continue;
						
			Pair<String, String> p1 = split(pair.getFirst());
			Pair<String, String> p2 = split(pair.getSecond());
			
			
			if (p1.getSecond().equalsIgnoreCase("complete") && 
					p2.getSecond().equalsIgnoreCase("complete")) {
				
				String left = p1.getFirst();
				String right = p2.getFirst();

				if (lengthOneLoops.contains(left)) {
					if (lengthOneLoops.contains(right))
						parallelRelations.put(left + LENGTH_ONE_SUFFIX, right + LENGTH_ONE_SUFFIX);
					parallelRelations.put(left + LENGTH_ONE_SUFFIX,  right);
				} 
				if (lengthOneLoops.contains(right))
					parallelRelations.put(left, right + LENGTH_ONE_SUFFIX);
				parallelRelations.put(left, right);
			}
		}
		/*System.out.println("Length-one loops: " + lengthOneLoops);
		System.out.println("Alpha concurrency: " + parallelRelations);*/
	}
	
	private Pair<String, String> split(XEventClass eventClass) {
		String label = eventClass.getId();
		StringTokenizer tokenizer = new StringTokenizer(label, "+");
		
		String eventName = tokenizer.nextToken();
		String lifeCycleLabel = tokenizer.nextToken();
		return new Pair<String, String>(eventName, lifeCycleLabel);
	}
	
	public boolean isEventInvolvedInLengthOneLoop(String eventName) {
		return lengthOneLoops.contains(eventName);
	}
	public boolean areConcurrent(String event1, String event2) {
		return parallelRelations.get(event1).contains(event2);
	}

	public Set<String> getLenghOneLoopEventLabels() {
		return lengthOneLoops;
	}
}
