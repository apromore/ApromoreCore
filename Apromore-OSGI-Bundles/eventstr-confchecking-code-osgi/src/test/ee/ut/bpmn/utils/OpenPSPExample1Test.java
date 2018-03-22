package ee.ut.bpmn.utils;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.UnfoldingPESSemantics;
import ee.ut.eventstr.comparison.OpenPartialSynchronizedProduct;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.Unfolder_PetriNet;

public class OpenPSPExample1Test {
	private String model = "cycle10";
	private String fileNameTemplate = "logs/%s.bpmn.mxml.gz";

	@Test
	public void test() throws Exception {
		PESSemantics<Integer> pes1 = getLogPESExample();
		UnfoldingPESSemantics<Integer> pes2 = getUnfoldingPESExample();
		
		OpenPartialSynchronizedProduct<Integer> psp = new OpenPartialSynchronizedProduct<Integer>(pes1, pes2);
		psp.perform().prune();
		
		IOUtils.toFile("psp.dot", psp.toDot());
	}
	
	public UnfoldingPESSemantics<Integer> getUnfoldingPESExample() throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("models/cycle10.bpmn"));
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
		return new UnfoldingPESSemantics<Integer>(pes.getPES(), pes);
	}

	public PESSemantics<Integer> getLogPESExample() throws Exception {
		XLog log = XLogReader.openLog(String.format(fileNameTemplate, model));		
		AlphaRelations alphaRelations = new AlphaRelations(log);
		
		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
		PORuns runs = new PORuns();

		for (XTrace trace: log) {
			PORun porun = new PORun(alphaRelations, trace);
			runs.add(porun);
		}
		
		IOUtils.toFile(model + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(model + "_merged.dot", runs.toDot());

		return new PESSemantics<Integer>(PORuns2PES.getPrimeEventStructure(runs, model));
	}
}
