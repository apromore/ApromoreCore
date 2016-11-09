package ee.ut.eventstr.comparison.differences;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.LinkedList;


/**
 * Collection of differences to be retrieved by the 
 * REST service. This class can be seen as a wrapper
 * for generating the JSON file retrieved by the 
 * REST service. 
 */

public class DifferencesML {
	private LinkedList<DifferenceML> differences;
	private int numberOfDifferences;
	private int commonLabels;

	public DifferencesML() {
		differences = new LinkedList<DifferenceML>();
		setNumberOfDifferences(0);
		commonLabels = 0;
	}

	public void add(DifferenceML diff) {
		if (!contains(diff)) {
			differences.add(diff);
			setNumberOfDifferences(differences.size());
		}
	}

	public LinkedList<DifferenceML> getDifferences() {
		return differences;
	}

	public void setDifferences(LinkedList<DifferenceML> differences) {
		this.differences = differences;
		setNumberOfDifferences(differences.size());
	}

	public String toString() {
		String result = "";

		for (DifferenceML dif : differences)
			result += dif.getSentence() + "\n";

		return result;
	}

	public static String toJSON(DifferencesML diffs) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			return mapper.writeValueAsString(diffs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void setCommonLabels(int commonLabels) {
		this.commonLabels = commonLabels;
	}

	public int getCommonLabels() {
		return commonLabels;
	}

	public int getNumberOfDifferences() {
		return numberOfDifferences;
	}

	public void setNumberOfDifferences(int numberOfDifferences) {
		this.numberOfDifferences = numberOfDifferences;
	}

	public boolean contains(DifferenceML difference) {
		for (DifferenceML diff : differences)
			if (diff.getSentence().equals(difference.getSentence()))
				return true;

		return false;
	}

	public DifferencesML clone() {
		DifferencesML diff = new DifferencesML();
		diff.setCommonLabels(commonLabels);

		for (DifferenceML d : differences)
			diff.add(d);

		return diff;
	}

	public void addAll(LinkedList<DifferenceML> differences) {
		for (DifferenceML d : differences)
			add(d);
	}
}
