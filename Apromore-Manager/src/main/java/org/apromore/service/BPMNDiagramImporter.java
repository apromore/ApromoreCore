package org.apromore.service;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * Created by Adriano on 29/10/2015.
 */
public interface BPMNDiagramImporter {
    public BPMNDiagram importBPMNDiagram(String xmlProcess);
}
