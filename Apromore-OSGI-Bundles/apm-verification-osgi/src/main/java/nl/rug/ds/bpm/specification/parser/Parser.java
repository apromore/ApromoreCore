package nl.rug.ds.bpm.specification.parser;

import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.specification.jaxb.*;
import nl.rug.ds.bpm.specification.map.SpecificationTypeMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Heerko Groefsema on 29-May-17.
 */
public class Parser {
	private static Pattern groupPattern = Pattern.compile("^([gG]roup).*");
	private static Pattern bracketPattern = Pattern.compile("\\((.*?)\\)");
	private static Pattern quotePattern = Pattern.compile("\"([^\"]*)\"");
	
	private EventHandler eventHandler;
	private SpecificationTypeMap specificationTypeMap;
	
	public Parser(EventHandler eventHandler, SpecificationTypeMap specificationTypeMap) {
		this.eventHandler = eventHandler;
		this.specificationTypeMap = specificationTypeMap;
	}
	
	public Specification parseSpecification(String specification) {
		Specification spec;
		Matcher bracketMatcher = bracketPattern.matcher(specification);
		if(bracketMatcher.find()) {
			String type = bracketMatcher.replaceFirst("").trim();
			String elements = bracketMatcher.group();
			elements = elements.substring(1, elements.length() - 1).trim();
			
			SpecificationType specificationType = specificationTypeMap.getSpecificationType(type);
			if(specificationType != null) {
				spec = new Specification();
				spec.setType(specificationType.getId());
				
				String[] arguments = elements.split(",");
				if(arguments.length == specificationType.getInputs().size()) {
					int i = 0;

					for (Input input : specificationType.getInputs()) {
						if (i < arguments.length) {
							InputElement element = new InputElement();
							element.setTarget(input.getValue());

							Matcher quoteMatcher = quotePattern.matcher(arguments[i].trim());
							if (quoteMatcher.matches()) {
								String id = quoteMatcher.group().trim();
								id = id.substring(1, id.length() - 1);
								element.setElement(id);
							} else
								element.setElement(arguments[i].trim());

							spec.addInputElement(element);
						} else {
							spec = null;
							eventHandler.logError("Failed to find enough arguments for command: " + specification);
							break;
						}
						i++;
					}
				}
				else {
					spec = null;
					eventHandler.logError("Failed to match specification type: " + specification);
				}
			}
			else {
				spec = null;
				eventHandler.logError("Failed to match specification type: " + specification);
			}
		}
		else {
			spec = null;
			eventHandler.logError("Failed to parse command: " + specification);
		}
		
		return spec;
	}
	
	public Group parseGroup(String group) {
		Group g;
		Matcher groupMatcher = groupPattern.matcher(group);
		if(groupMatcher.matches()) {
			g = new Group();
			
			Matcher bracketMatcher = bracketPattern.matcher(group);
			if(bracketMatcher.find()) {
				String elements = bracketMatcher.group();
				elements = elements.substring(1, elements.length() - 1).trim();
				String[] arguments = elements.split(",");
				
				if (arguments.length > 1) {
					g.setId(arguments[0]);
					
					for (int i = 1; i < arguments.length; i++) {
						Element element = new Element();
						Matcher quoteMatcher = quotePattern.matcher(arguments[i].trim());
						if (quoteMatcher.matches()) {
							String id = quoteMatcher.group().trim();
							id = id.substring(1, id.length() -1 );
							element.setId(id);
						}
						else
							element.setId(arguments[i].trim());
						
						g.addElement(element);
					}
				} else {
					g = null;
					eventHandler.logError("Failed to find enough arguments for command: " + group);
				}
			}
			else {
				g = null;
				eventHandler.logError("Failed to parse command: " + group);
			}
		}
		else {
			g = null;
			eventHandler.logError("Failed to parse command: " + group);
		}
		
		return g;
	}
}
