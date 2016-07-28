package ee.ut.eventstr.comparison.differences;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.jbpt.utils.IOUtils;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import ee.ut.bpmn.BPMNReader;
import ee.ut.nets.unfolding.Multiplicity;
import ee.ut.eventstr.PESSemantics;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import hub.top.petrinet.Node;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

public class ModelAbstractions {
	PetriNet net;
	BPMNReader reader;
	PESSemantics<Integer> pes;
	HashSet<String> labels;

	// Map from the nodes in the branching process to the net
	private HashMap<Node, Node> mapBP2Net;

	// Map from the nodes in the net system to the net
	private HashMap<Short, Node> mapSystem2Net;

	public ModelAbstractions(byte[] modelArray) throws JDOMException, IOException, SAXException, ParserConfigurationException {
		initReader(modelArray);

		labels = new HashSet<String>();
		labels.addAll(reader.getTaskLabels());
	}

	private void initReader(byte[] modelArray) {
		try {
			InputStream input = new ByteArrayInputStream(modelArray);

			this.reader = new BPMNReader(input);
			this.net = this.reader.getNet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HashSet<String> getLabels() {
		return labels;
	}

	public HashMap<Node, Node> getMapBP2Net() {
		return mapBP2Net;
	}

	public HashMap<Short, Node> getMapSystem2Net() {
		return mapSystem2Net;
	}

	// public void computePES(HashSet<String> commonLabels) throws SAXException,
	// ParserConfigurationException, IOException, JDOMException {
	// mapBP2Net = new HashMap<>();
	// mapSystem2Net = new HashMap<>();
	// HashSet<String> silent = new HashSet<String>(labels);
	// for (Transition t : reader.getPetriNet().getTransitions())
	// silent.add(t.getName());
	// silent.removeAll(commonLabels);
	//
	// this.pes = bpmnFoldedES.getPES(reader.getPetriNet(), commonLabels, name,
	// r.nextInt(), silent, false, mapBP2Net,
	// mapSystem2Net, ((BPMNReader) reader).mapNew2OldLbls);
	//
	// caseES = ES.PES;
	// }

	public String getName(String path) {
		String name = path.substring(path.lastIndexOf("/") + 1);
		
		return name;
	}

	public PESSemantics<Integer> getPES(Set<String> commonLabels) throws Exception {
		mapBP2Net = new HashMap<>();
		mapSystem2Net = new HashMap<>();
		
		if(pes != null)
			return this.pes;
		
		HashSet<String> silent = new HashSet<String>(labels);
		for (Transition t : net.getTransitions())
			silent.add(t.getName());
		silent.removeAll(commonLabels);
		
		
		Unfolding2PES unf2pes = getPES(commonLabels, silent, false, mapBP2Net, mapSystem2Net, ((BPMNReader)reader).mapNew2OldLbls);
		
//		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.EQUAL_PREDS);
//		unfolder.computeUnfolding();
//		
//		Unfolding2PES pes = new Unfolding2PES(unfolder, labels);

		HashMap<String, String> newOldLabels = ((BPMNReader)reader).mapNew2OldLbls;
		this.pes = new PESSemantics<Integer>(unf2pes.getPES());
		List<String> labelsPES = this.pes.getLabels();
		for(int i = 0; i < labelsPES.size(); i++){
			String label = labelsPES.get(i);
			if(newOldLabels.containsKey(label))
				labelsPES.set(i, newOldLabels.get(label));
		}
		
		return this.pes;
	}
	
	public Unfolding2PES getPES(Set<String> visibleLabels, HashSet<String> toOmit, boolean verbose,
			HashMap<Node, Node> originalMap, HashMap<Short, Node> mapDT, HashMap<String, String> mapNew2OldLbls)
			throws SAXException, ParserConfigurationException, IOException,
			JDOMException {
		try {
			File outputDir = new File("output");
			if (!outputDir.exists())
				outputDir.mkdir();
			 
			toOmit.removeAll(mapNew2OldLbls.keySet());
			
			// Unfolding
			Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.EQUAL_PREDS, toOmit);//new HashSet<String>());
			unfolder.computeUnfolding();

			visibleLabels.addAll(mapNew2OldLbls.keySet());
			
			HashMap<Node, Multiplicity> repetitions = new HashMap<>();
			PetriNet branchingProcess = unfolder.getUnfoldingAsPetriNet(visibleLabels, repetitions, originalMap, mapDT);
			
//			 if (verbose) {
//				// Write output files
//				IOUtils.toFile("output/Net.dot", net.toDot());
//				IOUtils.toFile("output/Unf.dot", unfolder.getUnfoldingAsPetriNet().toDot());
//				IOUtils.toFile("output/UnfPrefix.dot", branchingProcess.toDot());
//			 }
			
//			Unfolding2PES pes = new Unfolding2PES(unfolder, labels);
//
//			PreEventStructure<T> es = (PreEventStructure<T>) new PNet2PES(branchingProcess).getPrimeEventStructure();
//			es.setRepetitions((HashMap<T, Multiplicity>) repetitions);
//
//			toOmit = new HashSet<String>(es.getLabels());
//			toOmit.removeAll(visibleLabels);
//			toOmit.removeAll(mapNew2OldLbls.entrySet());
//			labelBack(es, mapNew2OldLbls);
//			
//			removeCuts(toOmit, visibleLabels);
//			
//			es.removeUnobservableEvents(toOmit);
//			
//			PrimeEventStructure<T> pes = es.toPES(); 
			// if (verbose) {
//			 PrintStream out = new PrintStream("target/tex/" + "" + id + "-Pes.tex");
//			 pes.toLatex(out, new LinkedList<T>());
//			 out.close();
			// }

//			return pes;
			
			return new Unfolding2PES(unfolder, labels);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public PESSemantics<Integer> getPES() {
		return pes;
	}

	public BPMNReader getReader() {
		return this.reader;
	}
	
	public PetriNet getNet(){
		return net;
	}
}
