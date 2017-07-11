package nl.rug.ds.bpm.pnml.reader;

import hub.top.petrinet.Node;
import hub.top.petrinet.Place;
import nl.rug.ds.bpm.extpetrinet.ExtPetriNet;
import nl.rug.ds.bpm.extpetrinet.ExtTransition;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public class ExtPNMLReader {
	public static ExtPetriNet parse(File file) throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(file);
		ExtPetriNet net = new ExtPetriNet();
		
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
			
			nodes.put(transId, net.addTransition(transId, label));
		}
		
		for (Object o: arcXPath.selectNodes(doc)) {
			Element el = (Element)o;
			Node src = nodes.get(el.getAttribute("source").getValue());
			Node tgt = nodes.get(el.getAttribute("target").getValue());
			if (src instanceof Place)
				net.addArc((Place)src, (ExtTransition)tgt);
			else
				net.addArc((ExtTransition)src, (Place)tgt);
		}
		
		return net;
	}
}
