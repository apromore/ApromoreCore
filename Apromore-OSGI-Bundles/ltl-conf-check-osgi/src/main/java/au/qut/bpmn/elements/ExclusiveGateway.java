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

package au.qut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class ExclusiveGateway extends Node {
	Transition transition;
	
	public ExclusiveGateway(Element element, PetriNet net) {
		super(element, net);
	}
	
	public void connectTo(Place place) {
		transition = new Transition();
		net.addFlow(inputPlace, transition);
		net.addFlow(transition, place);
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
		// TODO Auto-generated method stub
		return (org.jbpt.petri.Node) transition;
	}
}
