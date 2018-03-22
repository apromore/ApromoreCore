package au.ltl.domain;

public class Constraint {
	private String ltlFormula;
	private String constraintName;
	
	public Constraint(String ltlFormula, String constraintName) {
		this.ltlFormula = ltlFormula;
		this.constraintName = constraintName;
	}

	public String getLtlFormula() {
		return ltlFormula;
	}

	public void setLtlFormula(String ltlFormula) {
		this.ltlFormula = ltlFormula;
	}

	public String getConstraintName() {
		return constraintName;
	}

	public void setConstraintName(String constraintName) {
		this.constraintName = constraintName;
	}
	
	
}
