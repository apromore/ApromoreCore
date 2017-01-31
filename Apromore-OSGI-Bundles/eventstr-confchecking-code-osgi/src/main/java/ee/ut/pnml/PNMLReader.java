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

package ee.ut.pnml;

import hub.top.petrinet.Node;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public class PNMLReader {
	public static PetriNet parse(File file) throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(file);
		PetriNet net = new PetriNet();
		
		Map<String, Node> nodes = new HashMap<>();
		
		XPath placeXPath = XPath.newInstance("//place");
		XPath transitionXPath = XPath.newInstance("//transition");
		XPath arcXPath = XPath.newInstance("//arc");

		for (Object o: placeXPath.selectNodes(doc)) {
			Element el = (Element)o;
			String placeId = el.getAttribute("id").getValue();
			String label;
			
			try {
				label = el.getChild("name").getChild("text").getValue();
			}
			catch (Exception e) {
				label = placeId;
			}
			
			Place place = net.addPlace(label);
			if (el.getChild("initialMarking") != null)
				place.setTokens(1);
			nodes.put(placeId, place);
		}

		for (Object o: transitionXPath.selectNodes(doc)) {
			Element el = (Element)o;
			String transId = el.getAttribute("id").getValue();
			String label; 
			
			try {
				label = el.getChild("name").getChild("text").getValue();
			}
			catch (Exception e) {
				label = transId;
			}
			
			nodes.put(transId, net.addTransition(label));
		}
		
		for (Object o: arcXPath.selectNodes(doc)) {
			Element el = (Element)o;
			Node src = nodes.get(el.getAttribute("source").getValue());
			Node tgt = nodes.get(el.getAttribute("target").getValue());
			if (src instanceof Place)
				net.addArc((Place)src, (Transition)tgt);
			else
				net.addArc((Transition)src, (Place)tgt);
		}
		
		return net;
	}
}
