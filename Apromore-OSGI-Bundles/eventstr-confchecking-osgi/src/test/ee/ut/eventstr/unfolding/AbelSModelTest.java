package ee.ut.eventstr.unfolding;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.jdom.JDOMException;
import org.junit.Test;

import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;

public class AbelSModelTest {
	@Test
	public void testClass() throws Exception {
		NewUnfoldingPESSemantics<Integer> bpmnpes = getUnfoldingPESExample();
		
		new ElementaryCyclesFinder(bpmnpes);
	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample() throws JDOMException, IOException {
		PetriNet net = new PetriNet();
		Transition i = net.addTransition("i");
		Transition a = net.addTransition("a");
		Transition b = net.addTransition("b");
		Transition c = net.addTransition("c");
		Transition d = net.addTransition("d");
		Transition o = net.addTransition("o");
		Place p0 = net.addPlace("p0");
		Place p1 = net.addPlace("p1");
		Place p2 = net.addPlace("p2");
		Place p3 = net.addPlace("p3");
		Place p4 = net.addPlace("p4");
		Place p5 = net.addPlace("p5");
		Place p6 = net.addPlace("p6");
		Place p7 = net.addPlace("p7");
		
		net.addArc(p0, i);
		net.addArc(i, p1);
		net.addArc(i, p2);
		net.addArc(p1, a);
		net.addArc(p2, b);
		net.addArc(a, p3);
		net.addArc(b, p4);
		net.addArc(p3, c); net.addArc(p3, o);
		net.addArc(p4, c);
		net.addArc(c, p5);
		net.addArc(c, p6);
		net.addArc(p5, o);
		net.addArc(p6, d);
		net.addArc(d, p1); net.addArc(o, p7);
		
		net.setTokens(p0, 1);
		
		Set<String> labels = new HashSet<>(Arrays.asList("i", "a", "b", "c", "d", "o"));

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();

		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
//		IOUtils.toFile("bpmnpes.dot", pessem.toDot());
		return pessem;
	}
}
