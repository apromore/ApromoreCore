package ee.ut.bpmn.utils;

//import static org.hamcrest.MatcherAssert.assertThat;

import hub.top.petrinet.PetriNet;
import hub.top.uma.DNode;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import com.google.gwt.dev.util.collect.HashSet;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.Unfolder_PetriNet;

public class RunningExampleTest {

	@Test
	public void test() throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("models/simple/anothercycle.bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(0, 12);
		System.out.println(model.getLabels());
		
		Set<String> labels = new HashSet<String>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		PrimeEventStructure<Integer> pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels).getPES();
		
		IOUtils.toFile("pes.dot", pes.toDot());
	}

}
