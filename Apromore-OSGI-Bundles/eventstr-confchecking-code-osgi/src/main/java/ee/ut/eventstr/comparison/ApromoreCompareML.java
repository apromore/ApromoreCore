package ee.ut.eventstr.comparison;

import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.eventstr.comparison.differences.DifferencesML;
import ee.ut.eventstr.comparison.differences.ModelAbstractions;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.pnml.PNMLReader;

/**
 * @author Nick van Beest
 * @date 18/04/2016
 */
public class ApromoreCompareML {
	private long totalStartTime;

	public static final String version = "0.1";

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

	public static void main(String[] args) {
		String modelString = "models/btm.bpmn";
		String logString = "logs/btl.xes";

		HashSet<String> silents = new HashSet<String>();
		silents.add("_1_");
		silents.add("_0_");
		silents.add("t15");
		silents.add("t16");
		silents.add("t17");
		silents.add("t18");
        silents.add("t19");
		silents.add("t15");
        silents.add("t16");
        silents.add("t17");
        silents.add("t18");
        silents.add("t19");
        silents.add("t20");
        silents.add("t21");
        silents.add("t22");
        silents.add("t23");
		silents.add("t24");
        silents.add("t25");
		silents.add("null_positive");
		silents.add("null_enable");
		
		try {
			XLog log = XLogReader.openLog(logString);
			ModelAbstractions model = new ModelAbstractions(getFileAsArray(modelString));
			ApromoreCompareML comparator = new ApromoreCompareML();

			DiffMLGraphicalVerbalizer verbalizer = comparator.analyzeDifferences(model, log, silents);
			verbalizer.verbalize();
			System.out.println(DifferencesML.toJSON(verbalizer.getDifferences()));

//			NewDiffVerbalizer<Integer> verbalizer = comparator.analyzeDifferences(model.getNet(), log, silents);
//			verbalizer.verbalize();
//			System.out.println(verbalizer.getStatements());


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Set<String> getDifferences(PetriNet net, XLog log, HashSet<String> obs) {
		HashSet<String> silent = new HashSet<>();
		for (Transition t : net.getTransitions())
			if (!obs.contains(t.getName()))
				silent.add(t.getName());

		silent.add("_1_");
		silent.add("_0_");

		try {
			return runTest(net, log, silent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new HashSet<>();
	}
	
	public Set<String> getDifferencesSilent(PetriNet net, XLog log, HashSet<String> silent) {
		try {
			return runTest(net, log, silent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new HashSet<>();
	}

	// public Set<String> getDifferences(PetriNet net, XLog log, Set<String>
	// silents) {
	//
	// catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return new HashSet<String>();
	// }

	private Set<String> runTest(PetriNet net, XLog log, HashSet<String> silents) throws Exception {
		PrintStream stdout = System.out;
		ByteArrayOutputStream verbalization = new ByteArrayOutputStream();
		PrintStream verbout = new PrintStream(verbalization);
		System.setOut(verbout);

		NewDiffVerbalizer<Integer> verbalizer = analyzeDifferences(net, log, silents);

		verbalizer.verbalize();

		long totaltime = System.nanoTime() - totalStartTime;

		String statements = verbalization.toString();
		statements = statements.substring(statements.indexOf("================") + 17);
		Set<String> statementSet = removeDuplicateStatements(statements);
		statementSet.remove("");

		System.setOut(stdout);

		System.out.println("Total time: " + totaltime / 1000 / 1000 + "ms");

		return statementSet;
	}

	public DiffMLGraphicalVerbalizer analyzeDifferences(ModelAbstractions model, XLog log, HashSet<String> silents)
			throws Exception {
		DiffMLGraphicalVerbalizer verbalizer = new DiffMLGraphicalVerbalizer(model, log, silents);

//		SinglePORunPESSemantics<Integer> logpessem;
//		PrunedOpenPartialSynchronizedProduct<Integer> psp;
//
//		PrimeEventStructure<Integer> logpes = getLogPES(log);
//		NewUnfoldingPESSemantics<Integer> pnmlpes = getUnfoldingPES(model.getNet(), silents);
//		ExpandedPomsetPrefix<Integer> expprefix = new ExpandedPomsetPrefix<Integer>(pnmlpes);
//
//		PESSemantics<Integer> fullLogPesSem = new PESSemantics<Integer>(logpes);
//
//		DiffMLGraphicalVerbalizer verbalizer = new DiffMLGraphicalVerbalizer(fullLogPesSem, pnmlpes, expprefix);

		for (int sink : verbalizer.logpes.getSinks()) {
			SinglePORunPESSemantics<Integer> logpessem = new SinglePORunPESSemantics<Integer>(verbalizer.logpes, sink);
			PrunedOpenPartialSynchronizedProduct<Integer> psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, verbalizer.pes2);

			psp.perform().prune();

			verbalizer.addPSP(psp.getOperationSequence());
		}

		return verbalizer;
	}

	public NewDiffVerbalizer<Integer> analyzeDifferences(PetriNet net, XLog log, HashSet<String> silents)
			throws Exception {
		SinglePORunPESSemantics<Integer> logpessem;
		PrunedOpenPartialSynchronizedProduct<Integer> psp;

		PrimeEventStructure<Integer> logpes = getLogPES(log);
		NewUnfoldingPESSemantics<Integer> pnmlpes = getUnfoldingPES(net, silents);
		ExpandedPomsetPrefix<Integer> expprefix = new ExpandedPomsetPrefix<Integer>(pnmlpes);

		PESSemantics<Integer> fullLogPesSem = new PESSemantics<Integer>(logpes);
		NewDiffVerbalizer<Integer> verbalizer = new NewDiffVerbalizer<Integer>(fullLogPesSem, pnmlpes, expprefix);

		for (int sink : logpes.getSinks()) {
			logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);
			psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, pnmlpes);

			psp.perform().prune();

			verbalizer.addPSP(psp.getOperationSequence());
		}
		return verbalizer;
	}

	public NewDiffVerbalizer<Integer> analyzeDifferences(PetriNet net, PESSemantics pesLog, HashSet<String> silents)
			throws Exception {
		SinglePORunPESSemantics<Integer> logpessem;
		PrunedOpenPartialSynchronizedProduct<Integer> psp;

		PrimeEventStructure<Integer> logpes = pesLog.getPES();
		NewUnfoldingPESSemantics<Integer> pnmlpes = getUnfoldingPES(net, silents);
		ExpandedPomsetPrefix<Integer> expprefix = new ExpandedPomsetPrefix<Integer>(pnmlpes);

		PESSemantics<Integer> fullLogPesSem = pesLog;
		NewDiffVerbalizer<Integer> verbalizer = new NewDiffVerbalizer<Integer>(fullLogPesSem, pnmlpes, expprefix);

		for (int sink : logpes.getSinks()) {
			logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);
			psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, pnmlpes);

			psp.perform().prune();

			verbalizer.addPSP(psp.getOperationSequence());
		}
		return verbalizer;
	}

	private NewUnfoldingPESSemantics<Integer> getUnfoldingPES(PetriNet net, HashSet<String> silents) throws Exception {
		Set<String> labels = new HashSet<>();
		for (Transition t : net.getTransitions()) {
			if (!silents.contains(t.getName()) && t.getName().length() > 0) {
				labels.add(t.getName());
			}
		}

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA, silents);
		unfolder.computeUnfolding();

		Unfolding2PES pes = new Unfolding2PES(unfolder, labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		return pessem;
	}

	private PrimeEventStructure<Integer> getLogPES(XLog log) throws Exception {
		totalStartTime = System.nanoTime();

		AlphaRelations alphaRelations = new AlphaRelations(log);

		PORuns runs = new PORuns();

		Set<Integer> eventlength = new HashSet<Integer>();
		Set<Integer> succ;

		for (XTrace trace : log) {
			PORun porun = new PORun(alphaRelations, trace);

			runs.add(porun);

			succ = new HashSet<Integer>(porun.asSuccessorsList().values());
			eventlength.add(succ.size());

			runs.add(porun);
		}

		runs.mergePrefix();

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, "LOGPES");

		return pes;
	}

	private Set<String> removeDuplicateStatements(String verbalization) {
		List<String> tempstat = new ArrayList<String>(Arrays.asList(verbalization.split("\n")));
		Set<String> newstat = new HashSet<String>();

		int parpos, nextpos;
		String curstat;

		Set<String> toRemove = new HashSet<>();

		for (int i = 0; i < tempstat.size(); i++) {
			curstat = tempstat.get(i);
			parpos = curstat.indexOf("(", 1);

			while (parpos > 0) {
				nextpos = curstat.indexOf(")", parpos);
				curstat = curstat.substring(0, parpos - 1) + curstat.substring(nextpos);

				parpos = curstat.indexOf("(", parpos);
			}
			tempstat.set(i, curstat);

			if (curstat.contains("_1_") || curstat.contains("_0_"))
				toRemove.add(curstat);
		}

		newstat.addAll(tempstat);
		// newstat.removeAll(toRemove);

		return newstat;
	}
}
