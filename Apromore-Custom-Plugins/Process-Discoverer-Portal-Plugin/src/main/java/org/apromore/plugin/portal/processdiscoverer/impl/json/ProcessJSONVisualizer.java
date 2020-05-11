/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.plugin.portal.processdiscoverer.impl.json;

import org.apromore.plugin.portal.processdiscoverer.impl.layout.JGraphLayouter;
import org.apromore.plugin.portal.processdiscoverer.vis.Layouter;
import org.apromore.plugin.portal.processdiscoverer.vis.MissingLayoutException;
import org.apromore.plugin.portal.processdiscoverer.vis.ProcessVisualizer;
import org.apromore.plugin.portal.processdiscoverer.vis.UnsupportedElementException;
import org.apromore.plugin.portal.processdiscoverer.vis.VisualContext;
import org.apromore.plugin.portal.processdiscoverer.vis.VisualSettings;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The main class use to serialize a process map in JSON format
 * This JSON representation is then sent to browsers for visualization
 * 
 * @author Bruce Nguyen
 *
 */
public class ProcessJSONVisualizer implements ProcessVisualizer {
	private VisualToolkit visToolkit = new VisualToolkit();
	private Layouter layouter = new JGraphLayouter(); 
	
    @Override
    public String generateVisualizationText(Abstraction abs) throws Exception {
        VisualContext visContext = new VisualContext(abs);
        VisualSettings visSettings = new VisualSettings();
        
        long timer1 = System.currentTimeMillis();
        layouter.setVisualSettings(visSettings);
        layouter.layout(abs);
        System.out.println("Layout BPMNDiagram: " + (System.currentTimeMillis() - timer1) + " ms.");
        
        JSONArray json = generateJSON(abs, visContext, visSettings);
        return json.toString().replaceAll("'", "\\\\\'");
    }
    
	private JSONArray generateJSON(Abstraction abs, VisualContext visContext, 
	        VisualSettings visSettings) throws UnsupportedElementException, JSONException, MissingLayoutException {
		JSONArray jsonProcess = new JSONArray();
		
		for (BPMNNode node : abs.getDiagram().getNodes()) {
			ElementVisualizer nodeVisualizer = visToolkit.getVisualizer(node, visContext, visSettings);
			JSONObject jsonNode = nodeVisualizer.generateJSON(node);
			jsonProcess.put(jsonNode);
		}
		
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : abs.getDiagram().getEdges()) {
		    ElementVisualizer edgeVisualizer = visToolkit.getVisualizer(edge, visContext, visSettings);
			JSONObject jsonEdge = edgeVisualizer.generateJSON(edge);
			jsonProcess.put(jsonEdge);
		}
		
		return jsonProcess;
	}

    @Override
    public void cleanUp() {
        layouter.cleanUp();
    }

}
