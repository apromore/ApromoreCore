package au.ltl.bpmnConverter;
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


import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class ParallelGateway extends Node {
	private Transition positiveCase;

	public ParallelGateway(Element element, PetriNet net) {
		super(element, net);
		
		positiveCase = new Transition();
	}
	
	public Place getInputPlace() {
		Place place;
		if (net.getPreset(positiveCase).isEmpty())
			place = inputPlace;
		else
			place = new Place();
		
		net.addFlow(place, positiveCase);
		
		return place;
	}

	public void connectTo(Place place) {
		net.addFlow(positiveCase, place);
	}
	
	public Transition getTransition(){
		return positiveCase;
	}
}
