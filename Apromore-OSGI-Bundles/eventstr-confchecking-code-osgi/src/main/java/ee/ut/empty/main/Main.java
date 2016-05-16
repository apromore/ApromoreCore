package ee.ut.empty.main;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jdom.Element;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.bpmn.utils.BPMN2Reader;
import ee.ut.bpmn.utils.Petrifier;
import ee.ut.eventstr.comparison.ApromoreCompareML;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import hub.top.petrinet.PetriNet;

public class Main {

	public static void main(String[] args) throws Exception {
		String modelLog = "cycle10";
		String fileNameTemplate = "logs/%s.bpmn.mxml.gz";
		
		System.out.println("hello world");
		
		XLog log = XLogReader.openLog(String.format(fileNameTemplate, modelLog));		
		AlphaRelations alphaRelations = new AlphaRelations(log);
		
		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
	    long time = System.nanoTime();
		PORuns runs = new PORuns();

		for (XTrace trace: log) {
			PORun porun = new PORun(alphaRelations, trace);
			runs.add(porun);
		}
		
		runs.mergePrefix();
		System.out.println(runs.toDot());

		PORuns2PES.getPrimeEventStructure(runs, modelLog);
		
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("models/cycle10.bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(0, 12);
		System.out.println(model.getLabels());
		ApromoreCompareML comp = new ApromoreCompareML();
		System.out.println(comp.getDifferences(net, log));
		
	    System.out.println("Overall time: " + (System.nanoTime() - time) / 1000000000.0);
	}

}
