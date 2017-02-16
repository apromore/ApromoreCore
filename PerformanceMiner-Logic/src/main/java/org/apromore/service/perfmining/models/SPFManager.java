package org.apromore.service.perfmining.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.service.perfmining.filter.TraceAttributeFilterParameters;
import org.apromore.service.perfmining.parameters.SPFConfig;
import org.apromore.service.perfmining.models.SPF;

/**
 * This class manages all BPFs created in this plug-in
 * 
 * @author Hoang Nguyen
 * 
 */
public class SPFManager {
	private static SPFManager instance = null;

	// The first BPF in the list is the full one
	private final List<SPF> bpfList = new ArrayList<SPF>();

	//CaseID -> (AttributeName -> AttributeValue) 
	private Map<String, Map<String, String>> caseAttributesMap = new HashMap<String, Map<String, String>>();

	private int current = 0; //the current selected BPF to show on GUI

	protected SPFManager() {
		// Exists only to defeat instantiation.
	}

	public static SPFManager getInstance() {
		if (instance == null) {
			instance = new SPFManager();
		}
		return instance;
	}

	public SPF createSPF(SPFConfig config, TraceAttributeFilterParameters filter) throws Exception {
		SPF spf = new SPF(config, filter);
		spf.setIsFullBPF(filter.getFilter().isEmpty());
		bpfList.add(spf);
		setCurrentIndex(bpfList.size() - 1);
		return spf;
	}

	public void setCaseAttributeMap(Map<String, Map<String, String>> attMap) {
		caseAttributesMap = attMap;
	}

	public Map<String, Map<String, String>> getCaseAttributeMap() {
		return caseAttributesMap;
	}

	/**
	 * Select cases that satisfy a set of attribute values
	 * 
	 * @param attributeValueMap
	 *            : map of case attribute name to a set of attribute values
	 * @return: a set of case ids.
	 */
	public Set<String> selectCases(Map<String, Set<String>> attValueMap) {
		Set<String> result = new HashSet<String>();
		for (String caseID : caseAttributesMap.keySet()) {
			boolean attSatisfied = true;
			for (String attName : attValueMap.keySet()) {
				if (!caseAttributesMap.get(caseID).containsKey(attName)
						|| !attValueMap.get(attName).contains(caseAttributesMap.get(caseID).get(attName))) {
					attSatisfied = false;
					break;
				}
			}
			if (attSatisfied) {
				result.add(caseID);
			}
		}
		return result;
	}

	public int getCurrentIndex() {
		return current;
	}

	public void setCurrentIndex(int current) throws Exception {
		if ((current >= 0) && (current < bpfList.size())) {
			this.current = current;
		} else {
			throw new Exception("The selected index is out of range of BPF list");
		}
	}

	public SPF getCurrent() {
		return bpfList.get(current);
	}

	public int size() {
		return bpfList.size();
	}

	public SPF get(int index) throws Exception {
		if ((index >= 0) && (index < bpfList.size())) {
			return bpfList.get(index);
		} else {
			throw new Exception("The selected index " + index + " is out of range of BPF list");
		}
	}

	public void remove(int index) throws Exception {
		if ((index > 0) && (index < bpfList.size())) {
			bpfList.remove(index);
			current = 0;
		} else if (index == 0) {
			throw new Exception("Cannot delete the top(full) BPF");
		} else {
			throw new Exception("The selected index is out of range of BPF list");
		}
	}

	public void clear() {
		bpfList.clear();
		caseAttributesMap.clear();
	}

}
