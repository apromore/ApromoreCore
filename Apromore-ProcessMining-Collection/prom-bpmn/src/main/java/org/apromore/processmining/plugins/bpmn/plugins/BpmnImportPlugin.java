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
package org.apromore.processmining.plugins.bpmn.plugins;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.plugins.bpmn.Bpmn;
import org.apromore.processmining.plugins.bpmn.parameters.BpmnSelectDiagramParameters;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * @author Bruce Nguyen:
 * 	- 19 Oct 2021: add comments, clean up code
 */
public class BpmnImportPlugin {

	public BPMNDiagram importFromStreamToDiagram(InputStream input, String filename)  throws Exception {
	    Bpmn bpmnRaw = importFromStream(input, filename);
	    if (bpmnRaw.hasErrors()) throw new Exception("Errors occurred during import. Error messages: \n" + bpmnRaw.getErrorMessages());
	    return selectDefault(bpmnRaw);
	}

	private Bpmn importFromStream(InputStream input, String filename)
			throws Exception {
		/*
		 * Get an XML pull parser.
		 */
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		/*
		 * Initialize the parser on the provided input.
		 */
		xpp.setInput(input, null);
		/*
		 * Get the first event type.
		 */
		int eventType = xpp.getEventType();
		/*
		 * Create a fresh PNML object.
		 */
		Bpmn bpmn = new Bpmn();

		/*
		 * Skip whatever we find until we've found a start tag.
		 */
		while (eventType != XmlPullParser.START_TAG) {
			eventType = xpp.next();
		}
		/*
		 * Check whether start tag corresponds to PNML start tag.
		 */
		if (xpp.getName().equals(bpmn.tag)) {
			/*
			 * Yes it does. Import the PNML element.
			 */
			bpmn.importElement(xpp, bpmn);
		} else {
			/*
			 * No it does not. Return null to signal failure.
			 */
			bpmn.log(bpmn.tag, xpp.getLineNumber(), "Expected " + bpmn.tag + ", got " + xpp.getName());
		}
		if (bpmn.hasErrors()) {
		    throw new Exception("Errors while importing file " + filename + ". Errors were written to the system log file.");
		}
		return bpmn;
	}
	
	// Copied from BpmnSelectDiagramPlugin
    private BPMNDiagram selectDefault(Bpmn bpmn) {
        BpmnSelectDiagramParameters parameters = new BpmnSelectDiagramParameters();
        if (!bpmn.getDiagrams().isEmpty()) {
            parameters.setDiagram(bpmn.getDiagrams().iterator().next());
        } else {
            parameters.setDiagram(BpmnSelectDiagramParameters.NODIAGRAM);
        }
        return selectParameters(bpmn, parameters);
    }



	/**
	 * When the BPMN file contains a drawing section (BPMNDiagram tag), then only select elements
	 * present in this section to adhere to the file drawing. <br>
	 * When the file has no drawing section, then select all elements.<br>
	 * The drawing section is specified in BPMN 2.0 spec for interchangability of BPMN diagrams
	 * between drawing applications.
	 * @param bpmn
	 * @param parameters
	 * @return
	 */
	private BPMNDiagram selectParameters(Bpmn bpmn, BpmnSelectDiagramParameters parameters) {
        BPMNDiagram newDiagram = BPMNDiagramFactory.newBPMNDiagram("");
        Map<String, BPMNNode> id2node = new HashMap<String, BPMNNode>();
        Map<String, Swimlane> id2lane = new HashMap<String, Swimlane>();
        if (parameters.getDiagram() == BpmnSelectDiagramParameters.NODIAGRAM) {
            bpmn.unmarshall(newDiagram, id2node, id2lane);
        } else {
            Collection<String> elements = parameters.getDiagram().getElements();
            bpmn.unmarshall(newDiagram, elements, id2node, id2lane);
        }
        return newDiagram;
    }	
}
