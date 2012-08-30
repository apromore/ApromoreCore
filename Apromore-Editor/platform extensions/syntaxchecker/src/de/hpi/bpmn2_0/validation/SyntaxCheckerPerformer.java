package de.hpi.bpmn2_0.validation;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.generic.GenericDiagram;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import de.hpi.diagram.verification.SyntaxChecker;

public class SyntaxCheckerPerformer {

//	protected void processDocument(Document document, PrintWriter writer) {
//		String type = new StencilSetUtil().getStencilSet(document);
//		SyntaxChecker checker = null;
//		if(type != null){
//			if (type.equals("bpmn.json") || type.equals("bpmneec.json"))
//				checker = getCheckerBPMN(document);
//			else if (type.equals("bpmn1.1.json"))
//				checker = getCheckerBPMN11(document);
//			else if (type.equals("ibpmn.json"))
//				checker = getCheckerIBPMN(document);
//			else if (type.equals("interactionpetrinets.json"))
//				checker = getCheckerIPN(document);
//			else if (type.equals("epc.json"))
//				checker = getCheckerEPC(document);
//		}
//		
//		if(checker == null) {//try eRDF
//			try {
//				NamedNodeMap map = XPathAPI.selectSingleNode(document, "//a[@rel='oryx-stencilset']").getAttributes();
//				type = map.getNamedItem("href").getNodeValue();
//			} catch (TransformerException e) {
//				e.printStackTrace();
//			}
//			if(type != null && type.endsWith("petrinet.json")){
//				checker = getCheckerPetriNet(document);
//			}
//		}
//
//		if (checker == null) {
//			writer.print("{}");
//		} else {
//			checker.checkSyntax();
//			writer.print(checker.getErrorsAsJson().toString());
//		}
//	}
	
	public JSONObject processDocument(GenericDiagram diagram, List<Class<? extends AbstractBpmnFactory>> factoryClasses) throws JSONException, BpmnConverterException {
//		GenericDiagram diagram = DiagramBuilder.parseJson(jsonDocument);
		
		//TODO: validate edges that are not in the java object model
//		ArrayList<Shape> edges = this.getEdgesFromDiagram(diagram.getChildShapes());
		
		String type = diagram.getStencilsetRef().getNamespace();
		SyntaxChecker checker = null;
		
		if(type != null && (type.equals("http://b3mn.org/stencilset/bpmn2.0#") ||
				type.equals("http://b3mn.org/stencilset/bpmn2.0choreography#") ||
				type.equals("http://b3mn.org/stencilset/bpmn2.0conversation#"))) {
			checker = getCheckerBPMN2(diagram, factoryClasses);
		}
		
		if (checker == null) {
			return new JSONObject();
		} else {
			checker.checkSyntax();
			return checker.getErrorsAsJson();
		}
	}
	
//	private ArrayList<Shape> getEdgesFromDiagram(ArrayList<Shape> shapes) {
//		ArrayList<Shape> edges = new ArrayList<Shape>();
//		
//		for(Shape shape : shapes) {
//			String sid = shape.getStencilId();
//			
//			if(sid.equals("SequenceFlow")
//					|| sid.equals("MessageFlow")
//					|| sid.equals("Association_Undirected")
//					|| sid.equals("Association_Unidirectional")
//					|| sid.equals("Association_Bidirectional")) {
//				edges.add(shape);
//			} else if(shape.getChildShapes().size() > 0) {
//				edges.addAll(this.getEdgesFromDiagram(shape.getChildShapes()));
//			}
//			
//		}
//		
//		return edges;
//	}
	
	protected SyntaxChecker getCheckerBPMN2(GenericDiagram diagram, List<Class<? extends AbstractBpmnFactory>> factoryClasses) throws BpmnConverterException {
		Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, factoryClasses, false);
		
		Definitions defs = converter.getDefinitionsFromDiagram();
		return new BPMN2SyntaxChecker(defs);
	}
}
