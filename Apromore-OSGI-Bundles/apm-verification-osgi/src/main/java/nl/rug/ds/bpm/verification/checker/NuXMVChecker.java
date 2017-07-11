package nl.rug.ds.bpm.verification.checker;

import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.verification.formula.NuSMVFormula;
import nl.rug.ds.bpm.verification.model.kripke.Kripke;

import java.io.File;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class NuXMVChecker extends NuSMVChecker {


    public NuXMVChecker(EventHandler eventHandler, File checker, Kripke kripke, List<NuSMVFormula> formulas) {
        super(eventHandler, checker, kripke, formulas);
    }
}
