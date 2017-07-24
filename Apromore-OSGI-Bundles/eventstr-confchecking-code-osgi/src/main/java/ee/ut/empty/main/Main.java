/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package ee.ut.empty.main;

import java.io.File;
import java.util.HashSet;

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
		int iTrace = 0;

		for (XTrace trace: log) {
			PORun porun = new PORun(alphaRelations, trace, (iTrace++) + "");
			runs.add(porun);
		}
		
		runs.mergePrefix();
		System.out.println(runs.toDot());

		PORuns2PES.getPrimeEventStructure(runs, modelLog);
		
		HashSet<String> obsLabels = new HashSet<>();
		
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("bpm2014/model64.bpmn"));
		
		for(Integer i : model.getVisibleNodes())
			if(model.isTask(i) && !model.getName(i).isEmpty())
				obsLabels.add(model.getName(i));
		
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(0, 12);
		System.out.println(model.getLabels());
		ApromoreCompareML comp = new ApromoreCompareML();
		System.out.println(comp.getDifferences(net, log, obsLabels));
		
	    System.out.println("Overall time: " + (System.nanoTime() - time) / 1000000000.0);
	}

}
