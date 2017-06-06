package nl.rug.ds.bpm.verification.formula;

import nl.rug.ds.bpm.specification.jaxb.Specification;

/**
 * Created by p256867 on 13-4-2017.
 */
public class NuSMVFormula {
    private String formula;
    private Specification parent;

    public NuSMVFormula(String formula, Specification parent) {
        this.formula = formula;
        this.parent = parent;
    }

    public String getFormula() { return formula; }

    public Specification getSpecification() { return parent; }

    public boolean equals(String outputFormula) {
        return trimFormula(formula).equals(trimFormula(outputFormula));
    }

    private String trimFormula(String formula) {
        String f = formula.replace("CTLSPEC ", "");
        f = f.replace("LTLSPEC ", "");
        f = f.replace("JUSTICE ", "");
        return f.replaceAll("\\s+", "").trim();
    }
}
