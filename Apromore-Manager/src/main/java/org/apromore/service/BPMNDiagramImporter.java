package org.apromore.service;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;

/**
 * Created by Adriano on 29/10/2015.
 */
public interface BPMNDiagramImporter {
    BPMNDiagram importBPMNDiagram(String xmlProcess) throws Exception;
}
