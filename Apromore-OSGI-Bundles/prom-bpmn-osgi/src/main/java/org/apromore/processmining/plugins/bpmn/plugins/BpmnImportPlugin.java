package org.apromore.processmining.plugins.bpmn.plugins;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.plugins.bpmn.Bpmn;
import org.apromore.processmining.plugins.bpmn.parameters.BpmnSelectDiagramParameters;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class BpmnImportPlugin {

	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("BPMN 2.0 files", "bpmn", "xml");
	}
	
	public BPMNDiagram importFromStreamToDiagram(InputStream input, String filename)  throws Exception {
	    Bpmn bpmnRaw = importFromStream(input, filename);
	    return selectDefault(bpmnRaw);
	}

	protected Bpmn importFromStream(InputStream input, String filename) throws Exception {
		Bpmn bpmn = importBpmnFromStream(input, filename);
		if (bpmn == null) {
			/*
			 * No BPMN found in file. Fail.
		`	 */
			return null;
		}
		/*
		 * XPDL file has been imported. Now we need to convert the contents to a
		 * BPMN diagram.
		 */
//		BpmnDiagrams diagrams = new BpmnDiagrams();
//		diagrams.setBpmn(bpmn);
//		diagrams.setName(filename);
//		diagrams.addAll(bpmn.getDiagrams());
		return bpmn;
	}

	private Bpmn importBpmnFromStream(InputStream input, String filename)
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
    
    // Copied from BpmnSelectDiagramPlugin
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
