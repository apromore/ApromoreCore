package ee.ut.eventstr.unfolding;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.bpmn.utils.BPMN2Reader;
import ee.ut.bpmn.utils.Petrifier;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;

public class ElementaryCyclesFinderTest {
	@Test
	public void testClass() throws Exception {
		String bpmnfilename = 
//				"noloop"
//				"inner"
//				"outer"
//				"nested"
//				"overlapping"
				"CP_LGB"
//				"nested2"
//				"cycle10"
//				"crossed"
//				"concurrentcycle"
//				"concurrentcycle2"
//				"concurrentcycle3"
//				"concurrentcycle4"
//				"acyclic15p"
				;
				
		String bpmnfolder = 
				"models/RunningExample/"
//				"models/simple/"
//				"models/"
				;
		NewUnfoldingPESSemantics<Integer> bpmnpes = getUnfoldingPESExample(bpmnfilename, bpmnfolder);
		
		long time = System.nanoTime();
		new ElementaryCyclesFinder(bpmnpes);
		System.out.println("Time: " + (System.nanoTime() - time));
	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample(String filename, String folder) throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File(folder + filename + ".bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		
		Set<String> labels = new HashSet<String>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();

		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		IOUtils.toFile("bpmnpes.dot", pessem.toDot());
		return pessem;
	}
}
