package ee.ut.utilities.FES;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class ComparatorFreq {
	private FreqEventStructure fes1, fes2;
	private double zeroThreshold;
	private double diffThreshold;
	private Boolean combvalues;
	
	public ComparatorFreq(FreqEventStructure fes1, FreqEventStructure fes2) {
		this.fes1 = fes1;
		this.fes2 = fes2;
		
		// by default, the most elaborate comparison verbalisation is provided
		setZeroThreshold(0);
		setDiffThreshold(0);
		separateValues();
	}
	
	private double getTotalValue(Set<Double> valueset) {
		double totalvalue = 0;
		
		for (double val: valueset) {
			totalvalue += val;
		}
		
		return totalvalue;
	}
	
	private Set<FreqDifference> retainDifferences(FreqEventStructure fes1, FreqEventStructure fes2) {
		Set<FreqDifference> diffSet = new HashSet<FreqDifference>();
		Set<Double> val1, val2;
		double totalval1, totalval2;
		List<String> keys;
		int occ1, occ2;
		double relativediff;
		
		for (Entry<Set<String>, Set<Double>> entry: fes1.getEntrySet()) {
			val1 = entry.getValue();
			val2 = fes2.getFrequencyRelation(entry.getKey());
			
			// if one of the value sets is empty, then this concerns a control flow difference and should not be taken into account 
			// if the value sets are different, then the frequency of the branches to get to this event is also different 
			if ((val1 != null) && (val2 != null) && (entry.getKey().size() > 1) && (!val1.equals(val2))) {
				// if combvalues, differences where the combined frequencies are equal are excluded
				totalval1 = getTotalValue(val1);
				totalval2 = getTotalValue(val2);
				if ((!combvalues) || (totalval1 != totalval2)) {
					// zeroThreshold specifies the lowest combined frequencies to be included in the differences
					if ((totalval1 > zeroThreshold) && (totalval2 > zeroThreshold)) {
						occ1 = 0;
						occ2 = 0;
						keys = new ArrayList<String>(entry.getKey());
						if (fes1.isDuplicate(keys.get(0))) {
							occ1 = Integer.parseInt(keys.get(0).substring(keys.get(0).lastIndexOf("_") + 1));
						}
						if (fes1.isDuplicate(keys.get(keys.size() - 1))) {
							occ2 = Integer.parseInt(keys.get(keys.size() - 1).substring(
									    keys.get(keys.size() - 1).lastIndexOf("_") + 1)
								   );
						}
						
						if (totalval1 > totalval2) {
							relativediff = totalval2 / totalval1;
						}
						else {
							relativediff = totalval1 / totalval2;
						}
						
						relativediff = 1 - relativediff;
						
						if ((relativediff > diffThreshold) && (!entry.getKey().toString().contains("_0_")) && 
								(!entry.getKey().toString().contains("_1_")) && (!entry.getKey().toString().contains("_2_"))) {
							diffSet.add(new FreqDifference(entry.getKey(), fes1.getNewLabelCount(keys.get(0)), fes2.getNewLabelCount(keys.get(0)), val1, val2, occ1, occ2));
						}
					}
				}
			}	
		}
		
		return diffSet;
	}
	
	public void setZeroThreshold(double zeroThreshold) {
		this.zeroThreshold = zeroThreshold;
	}
	
	public double getZeroThreshold() {
		return zeroThreshold;
	}
	
	public void setDiffThreshold(double diffThreshold) {
		this.diffThreshold = diffThreshold;
	}
	
	public double getDiffThreshold() {
		return diffThreshold;
	}
	
	public void separateValues() {
		// transitive branching values are verbalised individually
		combvalues = false;
	}
	
	public void combineValues() {
		// transitive branching values are combined
		combvalues = true;
	}
	
	public String getDifferences(Boolean useQuotation) {
		String diff = "";
		
		for (FreqDifference d: retainDifferences(fes1, fes2)) {
			diff += d.verbalise(combvalues, useQuotation) + "\n";
		}
		
		return diff;
	}
	
	public String getPlainDifferences(Boolean useQuotation) {
		String diff = "";
		
		for (FreqDifference d: retainDifferences(fes1, fes2)) {
			diff += d.verbalisePlain() + "\n";
		}
		
		return diff;
	}
	
	public List<String> getIndividualDifferences(Boolean useQuotation) {
		List<String> diff = new ArrayList<String>();
		
		for (FreqDifference d: retainDifferences(fes1, fes2)) {
			diff.add(d.verbalise(combvalues, useQuotation));
		}
		
		return diff;
	}
}
