package org.oryxeditor.server.diagram.generic;

import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.label.Anchors.Anchor;
import org.oryxeditor.server.diagram.label.LabelSettings;
import org.oryxeditor.server.diagram.label.LabelStyle;

/**
 * Parses a given GenericDiagram into a JSONObject
 * 
 * @author Philipp Maschke
 */
public class GenericJSONBuilder {
	
	/**
	 * Parses the given diagram object into a JSON object
	 * @param diagram
	 * @return a JSON object representing the given diagram
	 * @throws JSONException
	 */
	public static <S extends GenericShape<S,D>, D extends GenericDiagram<S,D>> JSONObject parseModel(GenericDiagram<S,D> diagram) throws JSONException {
		return (new GenericJSONBuilder()).parseModelInternal(diagram);
	}
	

	public <S extends GenericShape<S,D>, D extends GenericDiagram<S,D>> JSONObject parse(GenericDiagram<S,D> diagram) throws JSONException {
		return parseModelInternal(diagram);
	}
	
	
	protected <S extends GenericShape<S,D>, D extends GenericDiagram<S,D>> JSONObject parseModelInternal(GenericDiagram<S,D> diagram) throws JSONException {//<S,D,?,?>
		JSONObject json = new JSONObject();
		
//		try {
			
			json.put("resourceId", 		diagram.getResourceId().toString());
			json.put("properties", 		parseProperties(diagram));
			json.put("stencil", 		parseStencil(diagram.getStencilId()));
			json.put("childShapes", 	parseChildShapesRecursive(diagram.getChildShapesReadOnly()));
			json.put("bounds", 			parseBounds(diagram.getBounds()));
			json.put("stencilset", 		parseStencilSet(diagram.getStencilsetRef()));
			json.put("ssextensions", 	parseStencilSetExtensions(diagram.getSsextensions()));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
		
		return json;
	}
	
	
	/**
	 * Delivers the correct JSON Object for the stencilId
	 * 
	 * @param stencilId
	 * @return
	 * @throws JSONException
	 */
	protected JSONObject parseStencil(String stencilId) throws JSONException {
		JSONObject stencilObject = new JSONObject();
		
		stencilObject.put("id", stencilId);
		
		return stencilObject;
	}
	
	
	/**
	 * Parses all child Shapes recursively and adds them to the correct
	 * JSON Object
	 * 
	 * @param childShapes
	 * @return
	 * @throws JSONException
	 */
	protected <S extends GenericShape<S,?>> JSONArray parseChildShapesRecursive(List<S> childShapes) throws JSONException {
		if(childShapes != null) {
			JSONArray childShapesArray = new JSONArray();
			
			for(S childShape : childShapes) {
				JSONObject childShapeObject = parseShape(childShape);
				childShapesArray.put(childShapeObject);
			}
			
			return childShapesArray;
		}
		
		return new JSONArray();
	}
	
	
	protected <S extends GenericShape<S,?>> JSONObject parseShape(S childShape) throws JSONException {
		JSONObject shapeJson = new JSONObject();				
		shapeJson.put("resourceId", 	childShape.getResourceId().toString());
		shapeJson.put("properties", 	parseProperties(childShape));
		shapeJson.put("stencil", 	parseStencil(childShape.getStencilId()));
		shapeJson.put("childShapes", parseChildShapesRecursive(childShape.getChildShapesReadOnly()));
		shapeJson.put("outgoing", 	parseOutgoings(childShape.getOutgoingsReadOnly()));
		shapeJson.put("bounds", 		parseBounds(childShape.getBounds()));
		shapeJson.put("dockers", 	parseDockers(childShape.getDockersReadOnly()));
		shapeJson.put("labels", 		parseLabelSettings(childShape.getLabelSettings()));
		if(childShape instanceof GenericEdge){
			if(((GenericEdge<S,?>)childShape).getTarget() != null) 
				shapeJson.put("target", parseTarget(((GenericEdge<S,?>)childShape).getTarget()));
		}
		
		return shapeJson;
	}


	protected JSONArray parseLabelSettings(Collection<LabelSettings> labelSettings) throws JSONException {
		JSONArray jsonLabels = new JSONArray();
		
		for (LabelSettings label: labelSettings){
			JSONObject jsonLabel = new JSONObject();
			
			if (label.getPosition() != null){
				jsonLabel.put("x", label.getPosition().getX());
				jsonLabel.put("y", label.getPosition().getY());
			}
			if (label.getDistance() != null){
				jsonLabel.put("distance", label.getDistance().doubleValue());
			}
			if (label.getReference() != null){
				jsonLabel.put("ref", label.getReference());
			}
			if (label.getFrom() != null){
				jsonLabel.put("from", label.getFrom());
			}
			if (label.getTo() != null){
				jsonLabel.put("to", label.getTo());
			}
			if (label.getAlignHorizontal() != null){
				jsonLabel.put("align", label.getAlignHorizontal().toString());
			}
			if (label.getAlignVertical() != null){
				jsonLabel.put("valign", label.getAlignVertical().toString());
			}
			if (label.getEdgePos() != null){
				jsonLabel.put("edge", label.getEdgePos().toString());
			}
			if (label.getOrientation() != null){
				jsonLabel.put("orientation", label.getOrientation().toString());
			}
				
			if (label.getAnchors().contains(Anchor.TOP)){
				jsonLabel.put("top", true);
			}
			if (label.getAnchors().contains(Anchor.RIGHT)){
				jsonLabel.put("right", true);
			}
			if (label.getAnchors().contains(Anchor.BOTTOM)){
				jsonLabel.put("bottom", true);
			}
			if (label.getAnchors().contains(Anchor.LEFT)){
				jsonLabel.put("left", true);
			}
			
			if (label.getStyle() != null)
				jsonLabel.put("styles", parseLabelStyle(label.getStyle()));
			
			jsonLabels.put(jsonLabel);
		}
		return jsonLabels;
	}

	
	protected JSONObject parseLabelStyle(LabelStyle style) throws JSONException {
		JSONObject jsonStyle = new JSONObject();
		
		if (style.getFontFamily() != null){
			jsonStyle.put("family", style.getFontFamily());
		}
		
		if (style.getFontSize() != null){
			jsonStyle.put("size", style.getFontSize());
		}
		
		if (style.isBold()){
			jsonStyle.put("bold", true);
		}
		
		if (style.isItalic()){
			jsonStyle.put("italic", true);
		}
		
		if (style.getFill() != null){
			jsonStyle.put("fill", "#" + Integer.toHexString(style.getFill().getRGB()).substring(2));
		}
		return jsonStyle;
	}

	
	/**
	 * Delivers the correct JSON Object for the target
	 * 
	 * @param target
	 * @return
	 * @throws JSONException
	 */
	protected <S extends GenericShape<S,?>> JSONObject parseTarget(S target) throws JSONException {
		JSONObject targetObject = new JSONObject();
		targetObject.put("resourceId", target.getResourceId().toString());
		return targetObject;
	}
	
	
	/**
	 * Delivers the correct JSON Object for the dockers
	 * 
	 * @param dockers
	 * @return
	 * @throws JSONException
	 */
	protected JSONArray parseDockers(List<Point> dockers) throws JSONException {
		if(dockers != null) {
			JSONArray dockersArray = new JSONArray();
			for(Point docker : dockers) {
				JSONObject dockerObject = new JSONObject();
				if (docker != null){
					dockerObject.put("x", docker.getX().doubleValue());
					dockerObject.put("y", docker.getY().doubleValue());
				}else{
					dockerObject.put("x", "null");
					dockerObject.put("y", "null");
				}
				dockersArray.put(dockerObject);
			}
			return dockersArray;
		}
		
		return new JSONArray();
	}
	
	
	/**
	 * Delivers the correct JSON Object for outgoings
	 * 
	 * @param outgoings
	 * @return
	 * @throws JSONException
	 */
	protected <S extends GenericShape<S,?>> JSONArray parseOutgoings(List<S> outgoings) throws JSONException {
		if(outgoings != null) {
			JSONArray outgoingsArray = new JSONArray();
			
			for(S outgoing : outgoings) {
				JSONObject outgoingObject = new JSONObject();
				
				outgoingObject.put("resourceId", outgoing.getResourceId().toString());
				outgoingsArray.put(outgoingObject);
			}
				
			return outgoingsArray;
		}
		
		return new JSONArray();
	}
	
	
	/**
	 * Delivers the correct JSON Object for properties
	 * 
	 * @param properties
	 * @return
	 * @throws JSONException
	 */
	protected JSONObject parseProperties(GenericShape<?, ?> shape) throws JSONException {
		if(shape != null) {
			JSONObject propertiesObject = new JSONObject();
			
			for (String name: shape.getPropertyNames()){
				Object value = shape.getPropertyObject(name);
				if (value instanceof Float)
					value = ((Float)value).doubleValue();
				
				propertiesObject.put(name, value);
			}
			
			return propertiesObject;
		}else		
			return new JSONObject();
	}
	
	/**
	 * Delivers the correct JSON Object for the Stencilset Extensions
	 * 
	 * @param extensions
	 * @return
	 */
	protected JSONArray parseStencilSetExtensions(List<String> extensions) {
		if(extensions != null) {
			JSONArray extensionsArray = new JSONArray();
			
			for(String extension : extensions) 
				extensionsArray.put(extension.toString());
			
			return extensionsArray;
		}
		
		return new JSONArray();
	}
	
	/**
	 * Delivers the correct JSON Object for the Stencilset
	 * 
	 * @param stencilSetRef
	 * @return
	 * @throws JSONException
	 */
	protected JSONObject parseStencilSet(StencilSetReference stencilSetRef) throws JSONException {
		if(stencilSetRef != null) {
			JSONObject stencilSetObject = new JSONObject();
			
//			stencilSetObject.put("uri", stencilSetRef.getUri().toString());
//			stencilSetObject.put("namespace", stencilSetRef.getNamespace().toString());
			
			stencilSetObject.put("url", stencilSetRef.getUrl() != null? stencilSetRef.getUrl().toString() : null);
			stencilSetObject.put("namespace", stencilSetRef.getNamespace() != null ? stencilSetRef.getNamespace().toString() : null);
			return stencilSetObject;
		}
		
		return new JSONObject();
	}
	
	/**
	 * Delivers the correct JSON Object for the Bounds
	 * 
	 * @param bounds
	 * @return
	 * @throws JSONException
	 */
	protected JSONObject parseBounds(Bounds bounds) throws JSONException {
		JSONObject boundsObject	= new JSONObject();
		JSONObject lowerRight 	= new JSONObject();
		JSONObject upperLeft  	= new JSONObject();
		
		if(bounds != null) {					
			lowerRight.put("x", bounds.getLowerRight().getX().doubleValue());
			lowerRight.put("y", bounds.getLowerRight().getY().doubleValue());
			
			upperLeft.put("x", bounds.getUpperLeft().getX().doubleValue());
			upperLeft.put("y", bounds.getUpperLeft().getY().doubleValue());
		}else{
			lowerRight.put("x", "null");
			lowerRight.put("y", "null");
			
			upperLeft.put("x", "null");
			upperLeft.put("y", "null");
		}
		
		boundsObject.put("lowerRight", lowerRight);
		boundsObject.put("upperLeft", upperLeft);
		
		return boundsObject;
	}
}
