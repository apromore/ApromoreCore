package nl.rug.ds.bpm.specification.map;

import nl.rug.ds.bpm.specification.jaxb.SpecificationType;

import java.util.HashMap;

/**
 * Created by Heerko Groefsema on 10-Apr-17.
 */
public class SpecificationTypeMap {
	private HashMap<String, SpecificationType> specificationTypes;
	
	public SpecificationTypeMap() {
		specificationTypes = new HashMap<>();
	}
	
	public void addSpecificationType(SpecificationType specificationType) {
		specificationTypes.put(specificationType.getId(), specificationType);
	}
	
	public SpecificationType getSpecificationType(String id) {
		return specificationTypes.get(id);
	}
}
