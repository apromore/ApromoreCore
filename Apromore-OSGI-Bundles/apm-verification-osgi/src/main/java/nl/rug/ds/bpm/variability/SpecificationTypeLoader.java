package nl.rug.ds.bpm.variability;

import nl.rug.ds.bpm.log.LogEvent;
import nl.rug.ds.bpm.log.Logger;
import nl.rug.ds.bpm.specification.jaxb.BPMSpecification;
import nl.rug.ds.bpm.specification.jaxb.Specification;
import nl.rug.ds.bpm.specification.jaxb.SpecificationSet;
import nl.rug.ds.bpm.specification.jaxb.SpecificationType;
import nl.rug.ds.bpm.specification.map.SpecificationTypeMap;
import nl.rug.ds.bpm.specification.marshaller.SpecificationUnmarshaller;

public class SpecificationTypeLoader {
	private SpecificationTypeMap specificationTypeMap;

	public SpecificationTypeLoader() {
		specificationTypeMap = new SpecificationTypeMap();

		loadSpecificationTypes();
	}

	public SpecificationTypeMap getSpecificationTypeMap() {
		return specificationTypeMap;
	}

	public SpecificationType getSpecificationType(String id) {
		return specificationTypeMap.getSpecificationType(id);
	}

	private void loadSpecificationTypes() {
		try {
//			InputStream targetStream = new FileInputStream("./resources/specificationTypes.xml");

			SpecificationUnmarshaller unmarshaller = new SpecificationUnmarshaller(this.getClass().getResourceAsStream("/resources/specificationTypes.xml"));
//			SpecificationUnmarshaller unmarshaller = new SpecificationUnmarshaller(targetStream);
			BPMSpecification specification = unmarshaller.getSpecification();

			for (SpecificationType specificationType: specification.getSpecificationTypes()) {
				specificationTypeMap.addSpecificationType(specificationType);
				Logger.log("Adding specification type " + specificationType.getId(), LogEvent.VERBOSE);
			}

			for (SpecificationSet set: specification.getSpecificationSets()) {
				for (Specification spec: set.getSpecifications()) {
					if(specificationTypeMap.getSpecificationType(spec.getType()) != null) {
						spec.setSpecificationType(specificationTypeMap.getSpecificationType(spec.getType()));
					}
					else {
						Logger.log("No such specification type: " + spec.getType(), LogEvent.WARNING);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
