package nl.rug.ds.bpm.variability;

import java.util.ArrayList;
import java.util.List;

import nl.rug.ds.bpm.specification.jaxb.Element;
import nl.rug.ds.bpm.specification.jaxb.Group;
import nl.rug.ds.bpm.specification.jaxb.InputElement;
import nl.rug.ds.bpm.specification.jaxb.Specification;

public class SpecificationBuilder {

	public static Specification getSpecification(String ctl, String specid, String specTypeId) {
		return getSpecification(ctl, specid, specTypeId, "");
	}
		
	public static Specification getSpecification(String ctl, String specid, String specTypeId, String groupId) {
		Specification spec = new Specification();
		
		spec.setId(specid);
		spec.setType(specTypeId);
		String target;
		
		switch(specTypeId) {
		case "AlwaysImmediateResponse":
			target = ctl.substring(ctl.indexOf("{") + 1, ctl.indexOf("}"));
			spec.addInputElement(new InputElement(target, "p"));
			
			target = ctl.substring(ctl.lastIndexOf("{") + 1, ctl.lastIndexOf("}"));
			spec.addInputElement(new InputElement(target, "q"));
			
			spec.addInputElement(new InputElement("silent", "s"));
			
			break;
		case "AlwaysImmediatePrecedence":
			target = ctl.substring(ctl.indexOf("{") + 1, ctl.indexOf("}"));
			spec.addInputElement(new InputElement(target, "p"));
			
			target = ctl.substring(ctl.lastIndexOf("{") + 1, ctl.lastIndexOf("}"));
			spec.addInputElement(new InputElement(target, "q"));
			
			break;
		case "ExistImmediateResponse":
			target = ctl.substring(ctl.indexOf("{") + 1, ctl.indexOf("}"));
			spec.addInputElement(new InputElement(target, "p"));
			
			target = ctl.substring(ctl.lastIndexOf("{") + 1, ctl.lastIndexOf("}"));
			spec.addInputElement(new InputElement(target, "q"));
			
			spec.addInputElement(new InputElement("silent", "s"));
			
			break;
		case "ExistResponse":
			target = ctl.substring(ctl.indexOf("{") + 1, ctl.indexOf("}"));
			spec.addInputElement(new InputElement(target, "p"));
			
			target = ctl.substring(ctl.lastIndexOf("{") + 1, ctl.lastIndexOf("}"));
			spec.addInputElement(new InputElement(target, "q"));
			
			break;
		case "AlwaysConflict":
			target = ctl.substring(ctl.indexOf("{") + 1, ctl.indexOf("}"));
			spec.addInputElement(new InputElement(target, "p"));
			
			target = ctl.substring(ctl.lastIndexOf("{") + 1, ctl.lastIndexOf("}"));
			spec.addInputElement(new InputElement(target, "q"));
			
			break;
		case "AlwaysParallel":
			target = groupId;
			spec.addInputElement(new InputElement(target, "p"));
						
			break;
		}
		
		return spec;
	}
	
	public static Group getGroup(String ctl) {
		Group group = new Group();
		
		List<String> el = new ArrayList<String>();
		
		String target;
		int pos = ctl.indexOf("{");
		
		while (pos >= 0) {
			target = ctl.substring(pos + 1, ctl.indexOf("}", pos));
			el.add(target);
			
			group.addElement(new Element(target));
			pos = ctl.indexOf("{", pos + 1);
		}
		
		group.setId("" + el.hashCode());
		
		return group;
	}
}
