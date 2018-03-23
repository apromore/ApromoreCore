package nl.rug.ds.bpm.verification.checker.nusmv2;

import nl.rug.ds.bpm.ConfigBean;
import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.exception.CheckerException;
import nl.rug.ds.bpm.log.LogEvent;
import nl.rug.ds.bpm.log.Logger;
import nl.rug.ds.bpm.specification.jaxb.Formula;
import nl.rug.ds.bpm.specification.jaxb.Specification;
import nl.rug.ds.bpm.verification.checker.Checker;
import nl.rug.ds.bpm.verification.checker.CheckerFormula;
import nl.rug.ds.bpm.verification.map.GroupMap;
import nl.rug.ds.bpm.verification.map.IDMap;
import nl.rug.ds.bpm.verification.model.kripke.Kripke;
import nl.rug.ds.bpm.verification.model.kripke.State;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by
 */
public class NuSMVChecker extends Checker {
	private File file;

	public NuSMVChecker(EventHandler eventHandler, File checker) {
		super(eventHandler, checker);
	}

	@Override
	public void addFormula(Formula formula, Specification specification, IDMap idMap, GroupMap groupMap) {
		NuSMVFormula nuSMVFormula = new NuSMVFormula(formula, specification, idMap, groupMap);
		formulas.add(nuSMVFormula);
		Logger.log("Including specification formula " + nuSMVFormula.getOriginalFormula(), LogEvent.VERBOSE);
	}

	@Override
	public void createModel(Kripke kripke) throws CheckerException {
		inputChecker.append("MODULE main\n");
		inputChecker.append(convertVAR(kripke));
		inputChecker.append(convertDEFINE(kripke));
		inputChecker.append(convertASSIGN(kripke));
		inputChecker.append(convertFORMULAS());

		try {
			file = File.createTempFile("model", ".smv");
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.println(inputChecker);
			writer.close();
		} catch (Throwable t) {
			throw new CheckerException("Failed to write to temporary file");
		}
	}

	protected Process createProcess() throws CheckerException {
		try {
			System.out.println("Start execution of NuSMV from " + ConfigBean.getNuSMVPath());
			File nusmv = new File(ConfigBean.getNuSMVPath());
			System.out.println(nusmv.getAbsolutePath() + " " + file.getAbsolutePath());
			Process proc = Runtime.getRuntime().exec(nusmv.getAbsolutePath() + " " + file.getAbsolutePath());
			return proc;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CheckerException("Could not call model checker NuSMV2");
		}
	}

	@Override
	public void checkModel() throws CheckerException {
		try {
			Process proc = createProcess();
			//Runtime.getRuntime().exec(executable.getAbsoluteFile() + " " + file.getAbsolutePath());

			List<String> results = getResults(proc);
			List<String> errors = getErrors(proc);

			parseResults(results);
			for (String line : errors)
				outputChecker.append(line + "\n");

			proc.waitFor();
			file.delete();
			proc.destroy();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new CheckerException("Failed to call NuSMV2");
		}
	}

	private String convertVAR(Kripke kripke) {
		StringBuilder v = new StringBuilder("\tVAR\n\t\t state:{");

		Iterator<State> i = kripke.getStates().iterator();
		while (i.hasNext()) {
			v.append(i.next().getID());
			if (i.hasNext()) v.append(",");
		}

		v.append("}; \n");

		return v.toString();
	}

	private String convertDEFINE(Kripke kripke) {
		StringBuilder d = new StringBuilder("\tDEFINE\n");

		Iterator<String> i = kripke.getAtomicPropositions().iterator();
		while (i.hasNext()) {
			String ap = i.next();
			d.append("\t\t " + ap + " := ");

			Iterator<State> j = findStates(kripke, ap).iterator();
			while (j.hasNext()) {
				State s = j.next();
				d.append("( state = " + s.getID() + " )");
				if (j.hasNext()) d.append(" | ");
			}
			d.append(";\n");
		}

		return d.toString();
	}

	private String convertASSIGN(Kripke kripke) {
		StringBuilder a = new StringBuilder("\tASSIGN\n\t\tinit(state) := {");

		//Safety
		for(State s: kripke.getSinkStates()) {
			s.addNext(s);
			s.addPrevious(s);
		}

		Iterator<State> i = kripke.getInitial().iterator();
		while (i.hasNext()) {
			a.append(i.next().getID());
			if (i.hasNext()) a.append(",");
		}
		a.append("};\n");

		a.append("\t\tnext(state) := \n\t\t\tcase\n");
		Iterator<State> j = kripke.getStates().iterator();
		while (j.hasNext()) {
			State s = j.next();
			a.append("\t\t\t\tstate = " + s.getID() + " : {");

			Iterator<State> k = s.getNextStates().iterator();
			if (k.hasNext())
				while (k.hasNext()) {
					a.append(k.next().getID());
					if (k.hasNext()) a.append(",");
				}
			a.append("};\n");
		}

		a.append("\t\t\tesac;\n");

		return a.toString();
	}

	private String convertFORMULAS() {
		StringBuilder f = new StringBuilder();
		for (CheckerFormula formula: formulas)
			f.append(formula.getCheckerFormula() + "\n");
		return f.toString();
	}

	private List<State> findStates(Kripke kripke, String ap) {
		List<State> sub = new ArrayList<State>(kripke.getStates().size() / kripke.getAtomicPropositions().size());

		for (State s : kripke.getStates())
			if (s.getAtomicPropositions().contains(ap))
				sub.add(s);

		return sub;
	}

	private List<String> getResults(Process proc) throws IOException {
		List<String> results = new ArrayList<>();

		String line = null;
		//inputStream
		InputStream stdin = proc.getInputStream();
		InputStreamReader in = new InputStreamReader(stdin);
		BufferedReader bir = new BufferedReader(in);
		while ((line = bir.readLine()) != null) {
			if (line.contains("-- specification "))
				results.add(line.replace("-- specification ", "").trim());
		}
		bir.close();
		in.close();

		return results;
	}

	private List<String> getErrors(Process proc) throws IOException {
		List<String> results = new ArrayList<>();

		String line = null;
		//errorstream
		InputStream stderr = proc.getErrorStream();
		InputStreamReader isr = new InputStreamReader(stderr);
		BufferedReader br = new BufferedReader(isr);
		while ((line = br.readLine()) != null) {
			results.add(line);
		}
		br.close();

		return results;
	}

	private void parseResults(List<String> resultLines) {
		Logger.log("Collecting results", LogEvent.INFO);
		for (String result: resultLines) {
			String formula = result;
			boolean eval = false;
			if (formula.contains("is false")) {
				formula = formula.replace("is false", "");
			} else {
				formula = formula.replace("is true", "");
				eval = true;
			}

			CheckerFormula abstractFormula = null;
			boolean found = false;
			Iterator<CheckerFormula> abstractFormulaIterator = formulas.iterator();
			while (abstractFormulaIterator.hasNext() && !found) {
				CheckerFormula f = abstractFormulaIterator.next();
				if(f.equals(formula)) {
					found = true;
					abstractFormula = f;
				}
			}

			if(!found) {
				if(eval)
					Logger.log("Failed to map " + formula + " to original specification while it evaluated true", LogEvent.WARNING);
				else
					Logger.log("Failed to map " + formula + " to original specification while it evaluated FALSE", LogEvent.ERROR);
			}
			else {
				String mappedFormula = abstractFormula.getOriginalFormula();
				eventHandler.fireEvent(abstractFormula.getSpecification(), abstractFormula, eval);
				if(eval)
					Logger.log("Specification " + abstractFormula.getSpecification().getId() + " evaluated true for " + mappedFormula, LogEvent.INFO);
				else
					Logger.log("Specification " + abstractFormula.getSpecification().getId() + " evaluated FALSE for " + mappedFormula, LogEvent.ERROR);
				formulas.remove(abstractFormula);
			}
		}
	}
}
