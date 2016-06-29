package ee.ut.eventstr.comparison.differences;

import java.util.LinkedList;

/**
 * Collection of runs associated to a difference. 
 * This is a wrapper used for generating the 
 * JSON file retrieved by the REST service. 
 */
public class Runs {
	LinkedList<Run> runs;

	public Runs(){
		runs = new LinkedList<Run>();
	}
	
	public void addRun(Run run) {
		runs.add(run);
	}
	
	public LinkedList<Run> getRuns() {
		return runs;
	}

	public void setRuns(LinkedList<Run> runs) {
		this.runs = runs;
	}
}
