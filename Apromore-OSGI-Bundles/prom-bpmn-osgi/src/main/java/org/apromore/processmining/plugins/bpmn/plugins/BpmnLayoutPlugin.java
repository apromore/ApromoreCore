package org.apromore.processmining.plugins.bpmn.plugins;

import java.io.ByteArrayInputStream;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.plugins.bpmn.BpmnDefinitions;

/**
 * Used to add layout information to the bpmnText without the layout part.
 *  
 * @author Bruce Nguyen
 *
 */
public class BpmnLayoutPlugin {
    public String addLayout(String bpmnText, String filename)  throws Exception {
        BpmnImportPlugin importer = new BpmnImportPlugin();
        BPMNDiagram d = importer.importFromStreamToDiagram(new ByteArrayInputStream(bpmnText.getBytes()), filename);
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
}
