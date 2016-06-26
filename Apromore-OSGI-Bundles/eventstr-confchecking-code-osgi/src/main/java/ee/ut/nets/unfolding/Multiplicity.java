package ee.ut.nets.unfolding;

public enum Multiplicity {
	ZERO,
	ONE,
	ZERO_ONE,
	ZERO_MORE,
	ONE_MORE,
	LOOP_ZERO_MORE,
	LOOP_ONE_MORE; // an event can be preceded by itself
	
	public static String getSymbol(Multiplicity multiplicity) {
		switch (multiplicity) {
		case LOOP_ZERO_MORE:
			return "*\\circlearrowright";
		case LOOP_ONE_MORE:
			return "+\\circlearrowright";
		case ONE:
			return "1";
		case ONE_MORE:
			return "+";
		case ZERO:
			return "0";
		case ZERO_MORE:
			return "*";
		case ZERO_ONE:
			return "0..1";
		default:
			break;
		}

		return "";
	}
}
