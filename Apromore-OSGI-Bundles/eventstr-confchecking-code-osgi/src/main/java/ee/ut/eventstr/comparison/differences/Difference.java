package ee.ut.eventstr.comparison.differences;


/**
 * This is the main container of a difference.
 * It contains a sentence, the runs in model 1 and 2
 * expressing the discovered difference between the 
 * models. 
 */

public class Difference {
	private String sentence;
	private Runs runsM1;
	private Runs runsM2;
	private Match m1;
	private Match m2;
	
	public Difference(){}

	public Difference(Runs runsM1, Runs runsM2) {
		this.runsM1 = runsM1;
		this.runsM2 = runsM2;
	}
	
	public Difference(String sentence, Match m1, Match m2) {
		this.sentence = sentence;
		this.m1 = m1;
		this.m2 = m2;
	}
	
	public Difference(String sentence) {
		this.sentence = sentence;
	}

	public void setSentence(String sentence){
		this.sentence = sentence;
	}
	
	public Runs getRunsM1() {
		return runsM1;
	}

	public void setRunsM1(Runs runsM1) {
		this.runsM1 = runsM1;
	}

	public Runs getRunsM2() {
		return runsM2;
	}

	public void setRunsM2(Runs runsM2) {
		this.runsM2 = runsM2;
	}

	public String getSentence() {
		return sentence;
	}
	
	public Match getM1() {
		return m1;
	}

	public Match getM2() {
		return m2;
	}
	
	public String toString(){
		return sentence;
	}
}
