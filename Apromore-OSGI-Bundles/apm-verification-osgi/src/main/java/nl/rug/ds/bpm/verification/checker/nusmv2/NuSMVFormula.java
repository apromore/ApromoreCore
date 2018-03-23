package nl.rug.ds.bpm.verification.checker.nusmv2;

import nl.rug.ds.bpm.specification.jaxb.Formula;
import nl.rug.ds.bpm.specification.jaxb.Specification;
import nl.rug.ds.bpm.verification.checker.CheckerFormula;
import nl.rug.ds.bpm.verification.map.GroupMap;
import nl.rug.ds.bpm.verification.map.IDMap;

/**
 * Created by p256867 on 13-4-2017.
 */
public class NuSMVFormula extends CheckerFormula {

    public NuSMVFormula(Formula formula, Specification specification, IDMap idMap, GroupMap groupMap) {
        super(formula, specification, idMap, groupMap);
    }

    @Override
    public String getCheckerFormula() {
        return formula.getLanguage() + " " + super.getCheckerFormula();
    }

    @Override
    public boolean equals(String outputFormula) {
        return trimFormula(super.getCheckerFormula()).equals(trimFormula(outputFormula));
    }

    private String trimFormula(String formula) {
        return formula.replaceAll("\\s+", "").trim();
    }
}
