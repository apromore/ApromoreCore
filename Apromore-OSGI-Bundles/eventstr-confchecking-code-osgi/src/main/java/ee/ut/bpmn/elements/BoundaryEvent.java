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

package ee.ut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class BoundaryEvent extends Node {
	private Transition eventOccurrence;
	private Transition subprocessCompletionSynchronisation;
	@SuppressWarnings("unused")
	private Place internalPlace;
	
	public BoundaryEvent(Element element, PetriNet net) {
		super(element, net);
		this.eventOccurrence = new Transition(element.getAttributeValue("name"));
		this.subprocessCompletionSynchronisation = new Transition();
		this.internalPlace = new Place();
		net.addFlow(eventOccurrence, inputPlace);
		net.addFlow(inputPlace, subprocessCompletionSynchronisation);
	}
	
	public void setOkPlace(Place ok) {
		inputPlace = ok;
		net.addFlow(inputPlace, eventOccurrence);
	}

	public void setNOkPlace(Place nok) {
		net.addFlow(eventOccurrence, nok);
		net.addFlow(nok, subprocessCompletionSynchronisation);
	}

	public void connectTo(Place place) {
		net.addFlow(subprocessCompletionSynchronisation, place);
	}
	
	public void synchroniseWith(Place place) {
		net.addFlow(place, subprocessCompletionSynchronisation);
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
		// TODO Auto-generated method stub
		return null;
	}
}
