package org.apromore.logman.stats.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.logman.LogManager;
import org.apromore.logman.log.classifieraware.IntLog;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;

public class CaseVariantStats extends StatsCollector {
	private IntLog intLog;
	private MutableList<IntArrayList> caseVariants;
	
	public CaseVariantStats(LogManager logManager) {
		if (logManager.getIntLog() == null) {
    		intLog = logManager.createIntLog();
    	}
    	caseVariants = intLog.distinct();
	}

    public MutableList<IntArrayList> getCaseVariants() {
    	return caseVariants;
    }
    
	public int countCaseVariant(IntArrayList caseVariant) {
		return intLog.count(c -> c.equals(caseVariant)); 
	}
    
	public List<String> getNamesFromCaseVariant(IntArrayList caseVariant) {
		List<String> stringVariant = new ArrayList<>();
		for (int i=0; i<caseVariant.size(); i++) {
			stringVariant.add(intLog.getName(i));
		}
		return stringVariant;
	}
    
	// Return a list of trace indexes
    public List<Integer> getIndexesFromCaseVariant(IntArrayList caseVariant) {
    	FastList<Integer> indexes = intLog.collectIf(trace -> trace.equals(caseVariant), trace -> intLog.indexOf(trace));
    	return indexes;
    }
    
    // return: List of trace indexes belonging to a case variant => CountOfCaseVariant 
    public Map<List<Integer>, Integer> getCaseVariantMap() {
    	Map<List<Integer>, Integer> variantMap = new HashMap<>();
    	for (IntArrayList variant : this.caseVariants) {
    		variantMap.put(this.getIndexesFromCaseVariant(variant), this.countCaseVariant(variant));
    	}
    	return variantMap;
    }	
}
