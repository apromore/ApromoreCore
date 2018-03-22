/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package ee.ut.bpmn.elements;

import java.util.HashMap;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class SimpleTask extends Node {
	protected Transition positiveCase;
		
	public SimpleTask(Element element, PetriNet net){// HashMap<String, String> tasks), HashMap<String, Integer> labelCounter) {
		super(element, net);
		String name = element.getAttributeValue("name");
		
//		if(tasks.containsKey(name))
//			name += labelCounter.get(name);
		
		this.positiveCase = new Transition(name);
				
		net.addFlow(inputPlace, positiveCase);
	}
	
	public void connectTo(Place place) {
		net.addFlow(positiveCase, place);
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
		return positiveCase;
	}	
	
}
