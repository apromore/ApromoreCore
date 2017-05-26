package nl.rug.ds.bpm.verification.checker;

import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.verification.formula.NuSMVFormula;
import nl.rug.ds.bpm.verification.model.kripke.Kripke;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public abstract class AbstractChecker {
    protected StringBuilder inputChecker;
    protected StringBuilder outputChecker;
    protected Kripke kripke;
    protected List<NuSMVFormula> formulas;
    protected File file, checker;
    protected EventHandler eventHandler;
    protected List<String> results;

    public AbstractChecker(EventHandler eventHandler, File checker, Kripke kripke, List<NuSMVFormula> formulas) {
        this.eventHandler = eventHandler;
        this.checker = checker;
        this.kripke = kripke;
        this.formulas = formulas;
        outputChecker = new StringBuilder();
        results = new ArrayList<>();
    }

    public abstract void createInputData();

    public String getInputChecker() {
        return inputChecker.toString();
    }

    public String getOutputChecker() {
        return outputChecker.toString();
    }

    protected String convertFORMULAS() {
        StringBuilder f = new StringBuilder();
        for (NuSMVFormula formula: formulas)
            f.append(formula.getFormula() + "\n");
        return f.toString();
    }

    protected void createInputFile() {
        try {
            file = File.createTempFile("model", ".smv");
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println(inputChecker);
            writer.close();
        } catch (Throwable t) {
            eventHandler.logCritical("Issue writing temporary file");
        }
    }

    public List<String> callModelChecker() {
        createInputFile();
        Process proc = createProcess();
        getInputStream(proc);

        return getResults(results);
    }

    abstract Process createProcess();

    abstract List<String> getResults(List<String> results);

    protected void getInputStream(Process proc) {
        try {
            String line = null;
            //inputStream
            InputStream stdin = proc.getInputStream();
            InputStreamReader in = new InputStreamReader(stdin);
            BufferedReader bir = new BufferedReader(in);
            while ((line = bir.readLine()) != null) {
                results.add(line);
            }
            bir.close();
            in.close();

            //errorstream
            InputStream stderr = proc.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                outputChecker.append(line + "\n");
            }

            br.close();
            proc.waitFor();
            file.delete();
            proc.destroy();

        } catch (Throwable t) {
            eventHandler.logError("Could not call model checker");
            eventHandler.logError("No checks were performed");
        }
    }
}
