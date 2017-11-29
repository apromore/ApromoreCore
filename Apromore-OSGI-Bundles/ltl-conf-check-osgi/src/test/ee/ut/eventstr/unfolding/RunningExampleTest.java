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

public class RunningExampleTest {
	@Test
	public void testClass() throws Exception {
		NewUnfoldingPESSemantics<Integer> bpmnpes = getUnfoldingPESExample();
	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample() throws JDOMException, IOException {
		PetriNet net = new PetriNet();
		Transition a = net.addTransition("A");
		Transition b = net.addTransition("B");
		Transition c = net.addTransition("C");
		Transition d = net.addTransition("D");
		Transition e = net.addTransition("E");
		Transition f = net.addTransition("F");
		Transition g = net.addTransition("G");
		Transition h = net.addTransition("H");
		Transition tau1 = net.addTransition("t1");
		Transition tau2 = net.addTransition("t2");
		Transition tau3 = net.addTransition("t3");
		Transition tau4 = net.addTransition("t4");
		Transition tau5 = net.addTransition("t5");
		Transition tau6 = net.addTransition("t6");
		Transition tau7 = net.addTransition("t7");
		Transition tau8 = net.addTransition("t8");
		Place p0 = net.addPlace("p0");
		Place p1 = net.addPlace("p1");
		Place p2 = net.addPlace("p2");
		Place p3 = net.addPlace("p3");
		Place p4 = net.addPlace("p4");
		Place p5 = net.addPlace("p5");
		Place p6 = net.addPlace("p6");
		Place p7 = net.addPlace("p7");
		Place p8 = net.addPlace("p8");
		Place p9 = net.addPlace("p9");
		Place p10 = net.addPlace("p10");
		Place p11 = net.addPlace("p11");
		Place p12 = net.addPlace("p12");
		Place p13 = net.addPlace("p13");
		Place p14 = net.addPlace("p14");
		
		net.addArc(p0, a);
		net.addArc(a, p1);
		net.addArc(p1, tau1);
		net.addArc(tau1, p2);
		net.addArc(tau1, p3);
		net.addArc(p2, b);
		net.addArc(p3, c);
		net.addArc(b, p4); net.addArc(c, p5);
		net.addArc(p4, tau2); net.addArc(p5, tau2);
		net.addArc(tau2, p6);
		net.addArc(p6, d); net.addArc(d, p7);
		net.addArc(p7, tau3); net.addArc(p7, tau4);
		net.addArc(tau3, p8); net.addArc(tau4, p9);
		net.addArc(p8, e); net.addArc(p9, f);
		net.addArc(e, p10); net.addArc(f, p11);
		net.addArc(p11, tau5); net.addArc(p11, g);
		net.addArc(tau5, p10); net.addArc(g, p12);
		net.addArc(p12, tau6); net.addArc(tau6, p10);
		net.addArc(p12, tau7); net.addArc(tau7, p6);
		
		net.addArc(p10, tau8); net.addArc(tau8, p13);
		net.addArc(p13, h); net.addArc(h, p14);
		
		net.setTokens(p0, 1);
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();

		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		
		return null;
	}
}
