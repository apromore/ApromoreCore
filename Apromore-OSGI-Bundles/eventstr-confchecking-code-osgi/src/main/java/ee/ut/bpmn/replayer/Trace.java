package ee.ut.bpmn.replayer;

import java.util.LinkedList;
import java.util.Set;

public class Trace<T> {
	LinkedList<T> log = new LinkedList<>();

	public Trace() {
		log = new LinkedList<>();
	}

	public Trace(LinkedList<T> strongCauses) {
		this.log = strongCauses;
	}
	
	public void addStrongCause(T s) {
		log.add(s);
	}
	
	public void addAllStrongCauses(Set<T> set) {
		log.addAll(set);
	}

	public Trace<T> clone(){
		return new Trace<>(new LinkedList<T>(log));
	}
	
	public LinkedList<T> getLog(){
		return log;
	}
}
