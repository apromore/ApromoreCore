package ee.ut.org.processmining.framework.util;

public class Pair<F, S> {

	protected final S second;
	protected final F first;

	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	private static boolean equals(Object x, Object y) {
		return ((x == null) && (y == null)) || ((x != null) && x.equals(y));
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object other) {
		return (other instanceof Pair) && equals(first, ((Pair<F, S>) other).first)
				&& equals(second, ((Pair<F, S>) other).second);
	}

	@Override
	public int hashCode() {
		if (first == null) {
			return second == null ? 0 : second.hashCode() + 1;
		} else {
			return second == null ? first.hashCode() + 2 : first.hashCode() * 17 + second.hashCode();
		}
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + ")";
	}

}