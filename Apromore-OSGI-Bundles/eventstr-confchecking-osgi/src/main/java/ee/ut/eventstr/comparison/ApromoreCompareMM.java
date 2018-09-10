/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package ee.ut.eventstr.comparison;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.bpmn.utils.BPMN2Reader;
import ee.ut.bpmn.utils.Petrifier;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.comparison.differences.Difference;
import ee.ut.eventstr.comparison.differences.Differences;
import ee.ut.eventstr.comparison.differences.ModelAbstractions;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;
import org.jdom.Element;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ApromoreCompareMM {

	public static void main(String[] args) throws Exception {
//		String modelName1 = "bpm2014/model77.bpmn";
//		String modelName2 = "bpm2014/model64.bpmn";
		String modelName1 = "problematic/TutorialPD1.bpmn";
		String modelName2 = "problematic/TutorialPreD1.bpmn";
		
		ModelAbstractions model1 = new ModelAbstractions(getFileAsArray(modelName1));
		ModelAbstractions model2 = new ModelAbstractions(getFileAsArray(modelName2));
		
		ApromoreCompareMM comparator = new ApromoreCompareMM();
		DiffMMGraphicalVerbalizer verbalizer = comparator.analyzeDifferences(model1, model2, new HashSet<String>(model1.getLabels()), new HashSet<String>(model2.getLabels()));
		verbalizer.verbalize();

		Differences diffs = verbalizer.getDifferences();
		for(Difference diff : diffs.getDifferences()){
			System.out.println(diff);
		}

//		System.out.println(Differences.toJSON(verbalizer.getDifferences()));
//		System.out.println(verbalizer.getStatements());
	}

	public static byte[] getFileAsArray(String fileName) {
		FileInputStream fileInputStream = null;
		File file = new File(fileName);

		try {
			byte[] bFile = new byte[(int) file.length()];

			// convert file into array of bytes
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();

			return bFile;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void example1() throws Exception {
		BPMNProcess<Element> model1 = BPMN2Reader.parse(new File("bpm2014/Model1.bpmn"));
		Petrifier<Element> petrifier1 = new Petrifier<Element>(model1);
		PetriNet net1 = petrifier1.petrify(model1.getSources().iterator().next(), model1.getSinks().iterator().next());
		HashSet<String> labels1 = new HashSet<>();// model1.getLabels().values());

		for (Integer i : model1.getVisibleNodes())
			labels1.add(model1.getLabels().get(i));

		BPMNProcess<Element> model2 = BPMN2Reader.parse(new File("bpm2014/Model2.bpmn"));
		Petrifier<Element> petrifier2 = new Petrifier<Element>(model2);
		PetriNet net2 = petrifier2.petrify(model2.getSources().iterator().next(), model2.getSinks().iterator().next());

		HashSet<String> labels2 = new HashSet<>();
		for (Integer i : model2.getVisibleNodes())
			labels2.add(model2.getLabels().get(i));

		ApromoreCompareMM comp = new ApromoreCompareMM();

	}

	// public Differences getDifferences(ModelAbstractions model1,
	// ModelAbstractions model2, HashSet<String> obs1, HashSet<String> obs2)
	// throws Exception {
	// DiffMMGraphicalVerbalizer verbalizer = analyzeDifferences(model1, model2,
	// obs1, obs2);
	// verbalizer.verbalize();
	//
	// Differences statements = verbalizer.getDifferences();
	//
	//// if(statements.isEmpty())
	//// statements.add("No difference was found between the models");
	//
	// return statements;
	// }

	// public Set<String> getDifferences(PetriNet net1, PetriNet net2,
	// HashSet<String> obs1, HashSet<String> obs2) throws Exception {
	// DiffMMVerbalizer<Integer> verbalizer = analyzeDifferences(net1, net2,
	// obs1, obs2);
	// verbalizer.verbalize();
	//
	// Set<String> statements = verbalizer.getStatements();
	//
	// if(statements.isEmpty())
	// statements.add("No difference was found between the models");
	//
	// return statements;
	// }

	// private DiffMMVerbalizer<Integer> analyzeDifferences(PetriNet net1,
	// PetriNet net2, HashSet<String> obs1, HashSet<String> obs2) throws
	// Exception {
	// Set<String> common = new HashSet<>(obs1);
	// common.retainAll(obs2);
	//
	// PESSemantics<Integer> pnmlpes1 = getUnfoldingPES(net1, common);
	// PESSemantics<Integer> pnmlpes2 = getUnfoldingPES(net2, common);
	//
	// PartialSynchronizedProduct<Integer> psp = new
	// PartialSynchronizedProduct<>(pnmlpes1, pnmlpes2);
	// PartialSynchronizedProduct<Integer> pre = psp.perform();
	//
	// HashSet<String> commonLabels = new HashSet<>(pnmlpes1.getLabels());
	// commonLabels.retainAll(pnmlpes2.getLabels());
	// commonLabels.remove("_0_");
	// commonLabels.remove("_1_");
	//
	// DiffMMVerbalizer<Integer> verbalizer = new
	// DiffMMVerbalizer<Integer>(pnmlpes1, pnmlpes2, commonLabels, obs1, obs2);
	// psp.setVerbalizer(verbalizer);
	// pre.prune();
	//
	// write(psp.toDot(), "psp.dot");
	//
	// return verbalizer;
	// }

	public DiffMMGraphicalVerbalizer analyzeDifferences(ModelAbstractions model1, ModelAbstractions model2,
			HashSet<String> obs1, HashSet<String> obs2) throws Exception {
		Set<String> common = new HashSet<>(obs1);
		common.retainAll(obs2);

		PESSemantics<Integer> pnmlpes1 = model1.getPESSemantics(common);
		PESSemantics<Integer> pnmlpes2 = model2.getPESSemantics(common);

		PartialSynchronizedProduct<Integer> psp = new PartialSynchronizedProduct<>(pnmlpes1, pnmlpes2);
		PartialSynchronizedProduct<Integer> pre = psp.perform();

//		write(psp.toDot(), "psp1.dot");
		
		HashSet<String> commonLabels = new HashSet<>(pnmlpes1.getLabels());
		commonLabels.retainAll(pnmlpes2.getLabels());
		commonLabels.remove("_0_");
		commonLabels.remove("_1_");

		DiffMMGraphicalVerbalizer verbalizer = new DiffMMGraphicalVerbalizer(model1, model2, commonLabels, obs1, obs2);
		psp.setVerbalizer(verbalizer);
		pre.prune();

		// DiffMMVerbalizer<Integer> verbalizer = new
		// DiffMMVerbalizer<Integer>(pnmlpes1, pnmlpes2, commonLabels, obs1,
		// obs2);
		// psp.setVerbalizer(verbalizer);
		// pre.prune();

//		write(psp.toDot(), "psp.dot");

		return verbalizer;
	}

	private void write(String toWrite, String fileName) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"));
			writer.write(toWrite);
		} catch (IOException ex) {
			// report
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private PESSemantics<Integer> getUnfoldingPES(PetriNet net, Set<String> obs) throws Exception {
		Set<String> labels = new HashSet<>();
		for (Transition t : net.getTransitions()) {
			if (obs.contains(t.getName()))
				labels.add(t.getName());
		}

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.EQUAL_PREDS, new HashSet<String>());
		unfolder.computeUnfolding();

		HashMap<String, String> map = new HashMap<>();
		for(Transition t : unfolder.getUnfoldingAsPetriNet().getTransitions())
			map.put(t.getName(), t.getName());

		Unfolding2PES pes = new Unfolding2PES(unfolder, labels, map);
		return new PESSemantics<Integer>(pes.getPES());
	}
}
