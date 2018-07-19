package ee.ut.bpmn.utils;

//import static org.hamcrest.MatcherAssert.assertThat;

import hub.top.petrinet.PetriNet;
import hub.top.uma.DNode;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import com.google.gwt.dev.util.collect.HashSet;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.Unfolder_PetriNet;

public class BPMNReaderTest {

	@Test
	public void test() throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("models/cycle10.bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(0, 12);
		System.out.println(model.getLabels());
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), new HashSet<>(Arrays.asList("start", "init", "A", "B", "C", "D", "end")));
	}

}
