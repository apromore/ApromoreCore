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

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.processmining.plugins.bpmn.BpmnDefinitions;

/**
 * Used to add layout information to the bpmnText without the layout part.
 *  
 * @author Bruce Nguyen
 *
 */
public class BpmnLayoutPlugin {
    
    public static String addLayout(BPMNDiagram d, String filename)  throws Exception {
        BpmnDefinitions.BpmnDefinitionsBuilder def = new BpmnDefinitions.BpmnDefinitionsBuilder(d);
        BpmnDefinitions definitions = new BpmnDefinitions("definitions", def);
        String exportedBPMN = definitions.exportElements();
        String layoutBpmnText = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n " +
                "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n " +
                "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n " +
                "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "targetNamespace=\"http://www.omg.org/bpmn20\"\n " +
                "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">") +
                exportedBPMN +
                "</definitions>";
        return layoutBpmnText;
    }
    
    public static String addLayout(String bpmnText, String filename)  throws Exception {
        BPMNDiagram d = BPMNDiagramFactory.newDiagramFromProcessText(bpmnText);
        return addLayout(d, filename);
    }
}
