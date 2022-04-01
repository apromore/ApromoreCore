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

package org.apromore.plugin.portal.processdiscoverer.impl.json;

import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.plugin.portal.processdiscoverer.impl.layout.JGraphLayouter;
import org.apromore.plugin.portal.processdiscoverer.vis.InvalidOutputException;
import org.apromore.plugin.portal.processdiscoverer.vis.Layouter;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class use to serialize a process map in JSON format
 * This JSON representation is then sent to browsers for visualization
 * 
 * @author Bruce Nguyen
 *
 */
public class ProcessJSONVisualizer implements ProcessVisualizer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessJSONVisualizer.class);

	private VisualToolkit visToolkit = new VisualToolkit();
	private Layouter layouter = new JGraphLayouter();

    public ProcessJSONVisualizer() {
        super();
    }

    @Override
    public String generateVisualizationText(Abstraction abs, UserOptionsData userOptions) throws Exception {
        VisualContext visContext = new VisualContext(abs);
        VisualSettings visSettings = new VisualSettings(userOptions.getCostTable().getCurrency());
        
        long timer1 = System.currentTimeMillis();
        layouter.setVisualSettings(visSettings);
        layouter.layout(abs);
        LOGGER.debug("Layout BPMNDiagram: {} ms.", System.currentTimeMillis() - timer1);
        
        timer1 = System.currentTimeMillis();
        JSONArray json = generateJSON(abs, visContext, visSettings);
        LOGGER.debug("Generate JSON data from BPMNDiagram: {} ms.", System.currentTimeMillis() - timer1);
        return json.toString().replaceAll("'", "\\\\\'");
    }
    
	private JSONArray generateJSON(Abstraction abs, VisualContext visContext,
	        VisualSettings visSettings) throws UnsupportedElementException, JSONException, InvalidOutputException {
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
        System.out.println("ProcessVisualizer cleanup is done!");
    }

}
