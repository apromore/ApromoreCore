package ee.ut.eventstr.comparison.differences;

public class Match {
	Object e1;
	Object e2;
	
	public Match(Object event1, Object event2) {
		this.e1 = event1;
		this.e2 = event2;
	}

	public boolean isEqual(Match m1) {
		if(e1.equals(m1.e1) && e2.equals(m1.e2))
			return true;
		
		return false;
	}

}
