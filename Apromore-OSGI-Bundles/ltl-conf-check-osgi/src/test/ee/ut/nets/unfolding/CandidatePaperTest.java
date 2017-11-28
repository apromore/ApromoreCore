package ee.ut.nets.unfolding;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import org.jbpt.utils.IOUtils;
import org.junit.Test;

import ee.ut.nets.unfolding.BPstructBP.MODE;

public class CandidatePaperTest {
	@Test
	public void fistTrial() {
		PetriNet net = new PetriNet();
		Place p0 = net.addPlace("p0");
		Place p1 = net.addPlace("p1");
		Place p2 = net.addPlace("p2");
		Place p3 = net.addPlace("p3");
		Place p4 = net.addPlace("p4");
		Place p5 = net.addPlace("p5");
		
		Transition t0 = net.addTransition("A");
		Transition t1 = net.addTransition("B");
		Transition t2 = net.addTransition("D");
		Transition t3 = net.addTransition("C");
		Transition tau = net.addTransition("tau");
		Transition t4 = net.addTransition("E");

		net.addArc(p0, t0);
		net.addArc(t0, p1);
		net.addArc(t0, p2);
		net.addArc(p1, t1);
		net.addArc(p1, t2);
		net.addArc(p2, t2);
		net.addArc(p2, t3);
		net.addArc(p2, tau);
		
		net.addArc(t1, p3);
		net.addArc(t2, p3);
		net.addArc(t2, p4);
		net.addArc(t3, p4);
		net.addArc(tau, p4);
		
		net.addArc(p3, t4);
		net.addArc(p4, t4);
		net.addArc(t4, p5);
		
		p0.setTokens(1);
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		IOUtils.toFile("net1.dot", net.toDot());
		IOUtils.toFile("bp1.dot", unfolder.getUnfoldingAsDot());
	}
}
