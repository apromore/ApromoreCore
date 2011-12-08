package org.oryxeditor.server.diagram.generic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.label.Anchors;
import org.oryxeditor.server.diagram.label.Anchors.Anchor;
import org.oryxeditor.server.diagram.label.EdgePosition;
import org.oryxeditor.server.diagram.label.HorizontalAlign;
import org.oryxeditor.server.diagram.label.LabelOrientation;
import org.oryxeditor.server.diagram.label.LabelSettings;
import org.oryxeditor.server.diagram.label.LabelStyle;
import org.oryxeditor.server.diagram.label.VerticalAlign;

/**
 * Parses a BasicDiagram out of a given JSON string or object.
 * <p/>
 * Subclasses <b>MUST</b> override {@link #createNewDiagram(String)}, {@link #createNewEdge(String)} and {@link #createNewNode(String)} according to the type of shapes they need!
 * @author Philipp Maschke
 *
 * @param <S> the actual type of shape to be used (must inherit from {@link GenericShape}); calls to {@link GenericShape#getChildShapesReadOnly()}, ... will return this type
 * @param <D> the actual type of diagram to be used (must inherit from {@link GenericDiagram}); {@link GenericShape#getDiagram()} will return this type
 */
public abstract class GenericDiagramBuilder 
	<S extends GenericShape<S,D>, D extends GenericDiagram<S,D>, E extends GenericEdge<S, D>, N extends GenericNode<S, D>> 
	implements ShapeFactory<S, D, E, N>{

	private static final Logger LOGGER = Logger.getLogger(GenericDiagramBuilder.class);

	protected String parseStencilsetNamespaceInternal(JSONObject json) throws JSONException{
		if (json != null && json.has("stencilset")) {
			JSONObject jsonStencilset = json.getJSONObject("stencilset");

			if (jsonStencilset.has("namespace"))
				return jsonStencilset.getString("namespace").trim();
		}
		return null;
	}
	
	
	protected String parseDiagramIdInternal(JSONObject json) throws JSONException, IllegalArgumentException {
		if (json == null)
			throw new IllegalArgumentException("JSON object is null");
		String id = "canvas";
		if (json.has("resourceId")) {
			id = json.getString("resourceId");
		}
		return id;
	}
	
	
	/**
	 * Parses the json object to the diagram model, assumes that the json is hierarchical ordered
	 * 
	 * @param json
	 *            hierarchical JSON object representing a diagram
	 * @return a diagram object with all shapes as defined in json
	 * @throws JSONException
	 *             if JSON could not be parsed correctly
	 * @throws IllegalArgumentException
	 *             if json is null
	 */
	public D parse(JSONObject json) throws JSONException {
		if (json == null)
			throw new IllegalArgumentException("JSON object is null");

		Map<String, S> shapesMap = new HashMap<String, S>();
		Map<String, String> targetsMap = new HashMap<String, String>();
		Map<String, List<String>> outgoingsMap = new HashMap<String, List<String>>();
		Map<String, List<String>> childsMap = new HashMap<String, List<String>>();
		Map<String, JSONObject> flatJSON = flatRessources(json);

		// diagram specific parsing
		String id = "canvas";
		if (json.has("resourceId")) {
			id = json.getString("resourceId");
		}
		flatJSON.remove(id);
		D diagram = createNewDiagram(id);
		S diagramShape = (S) diagram;//only to reduce the number of warnings
		
		parseStencilSet(json, diagram);
		parseSsextensions(json, diagram);
		parseStencil(json, diagramShape);//, diagram.getStencilsetRef());
		parseProperties(json, diagramShape);
		parseChildShapes(json, diagramShape, childsMap);
		parseBounds(json, diagramShape);
		
		shapesMap.put(diagram.getResourceId(), diagramShape);

		//parse all shapes and add them to the map
		for (Map.Entry<String, JSONObject> entry : flatJSON.entrySet()) {
			shapesMap.put(entry.getKey(), 
				parseShape(entry.getKey(), entry.getValue(), 
					diagram, targetsMap, outgoingsMap, childsMap));
		}

		// setting these has been deferred so far
		fillInOutgoings(shapesMap, outgoingsMap);
		fillInTargets(shapesMap, targetsMap);
		fillInChildren(shapesMap, childsMap);
//		fillInIncomings(shapesMap.values());
		fillSources(shapesMap.values());

		return diagram;
	}
	
	
	/**
	 * Set the child relation based on childMap's entries.
	 * 
	 * @param shapesMap
	 * @param childMap
	 */
	protected void fillInChildren(Map<String,S> shapesMap, Map<String, List<String>> childMap) {
		// iterate through the existing shape IDs,
		// iterate through its child shapes' IDs, and
		// add the looked-up children to the looked-up shape
		for (Entry<String, List<String>> childsEntry: childMap.entrySet()) {
			S shape = shapesMap.get(childsEntry.getKey());
			for (String childId : childsEntry.getValue()) {
				S childShape = shapesMap.get(childId);
				shape.addChildShape(childShape);
			}
		}
	}


	/**
	 * Set the target relation based on targetMap's entries.
	 * 
	 * @param shapesMap
	 * @param targetMap
	 */
	protected void fillInTargets(Map<String,S> shapesMap, Map<String, String> targetMap) {
		// iterate through the existing shape - target mappings,
		// set the looked-up target to the looked-up shape
		for (Entry<String, String> targetEntry : targetMap.entrySet()) {
			S shape = shapesMap.get(targetEntry.getKey());
			if (shape != null && shape instanceof GenericEdge){
				S targetShape = shapesMap.get(targetEntry.getValue());
				if (targetShape != null)
					((GenericEdge<S, D>)shape).connectToATarget(targetShape);
			}
		}
	}


	/**
	 * Set the outgoing relation based on outgoingMap's entries.
	 * 
	 * @param shapesMap
	 * @param outgoingMap
	 */
	protected void fillInOutgoings(Map<String,S> shapesMap, Map<String, List<String>> outgoingMap) {
		// iterate through the existing shape IDs,
		// iterate through its child shapes' IDs, and
		// add the looked-up children to the looked-up shape
		for (Entry<String, List<String>> outgoingsEntry : outgoingMap.entrySet()) {
			S shape = shapesMap.get(outgoingsEntry.getKey());
			for (String outgoingId : outgoingsEntry.getValue()) {
				S outgoingShape = shapesMap.get(outgoingId);
				shape.addOutgoingAndUpdateItsIncomings(outgoingShape);	
			}
		}

	}


	protected void fillSources(Collection<S> shapes) {
		for (S s : shapes) {
			if (s instanceof GenericEdge) {
				S source = determineSource((GenericEdge<S, D>) s);
				if (source != null)
					((GenericEdge<S, D>) s).connectToASource(source);
			}
		}

	}


	/**
	 * A source of an edge is the one incoming shape, that doesn't have that shape as target
	 * 
	 * @param s
	 * @return
	 */
	protected S determineSource(GenericEdge<S, D> s) {
		if (s.getIncomingsReadOnly() == null || s.getIncomingsReadOnly().size() == 0)
			return null;
		else if (s.getIncomingsReadOnly().size() == 1) {
			S incoming = s.getIncomingsReadOnly().get(0);
			if (incoming instanceof GenericEdge && ((GenericEdge<S,D>) incoming).getTarget() != null
					&& ((GenericEdge<S,D>) incoming).getTarget().equals(s))
				return null;
			else
				return incoming;
		} else {// if more than one incomings return the one that doesn't have
			// 'this' as target (there should only be one!)
			List<S> incomings = new ArrayList<S>(s.getIncomingsReadOnly());
			Iterator<S> it = incomings.iterator();
			while (it.hasNext()) {
				S incoming = it.next();
				if (incoming instanceof GenericEdge) {
					S target = ((GenericEdge<S,D>) incoming).getTarget();
					if (target != null && target.equals(s))
						it.remove();
				}
			}

			if (incomings.isEmpty())
				return null;
			else if (incomings.size() == 1)
				return incomings.get(0);
			else
				throw new IllegalArgumentException("Shape '" + s.getResourceId() + "' has more than one source: "
						+ Arrays.toString(incomings.toArray()));
		}
	}


//	protected void fillInIncomings(Collection<S> shapes) {
//		for (S s : shapes) {
//			for (S o : s.getOutgoingsReadOnly()) {
//				if (!o.hasIncoming(s)) {
//					o.addIncoming(s);
//				}
//			}
//		}
//	}


	/**
	 * Parse one resource to a shape object and add it to the shapes array
	 * 
	 * @param resourceId
	 * @param jsonShape
	 * @param diagram
	 * @param targetMap
	 * @param outgoingMap
	 * @param childMap
	 * 
	 * @throws JSONException
	 */
	protected S parseShape(String resourceId, JSONObject jsonShape,
			D diagram, Map<String, String> targetMap,
			Map<String, List<String>> outgoingMap, Map<String, List<String>> childMap) throws JSONException {
		List<Point> dockers = getDockers(jsonShape, resourceId);
		S currentShape;
		if (GenericShapeImpl.isEdge(dockers))
			currentShape = (S) createNewEdge(resourceId);
		else
			currentShape = (S) createNewNode(resourceId);

		// parse all fields
		currentShape.setDockers(dockers);
		parseStencil(jsonShape, currentShape);//, diagram.getStencilsetRef());
		parseProperties(jsonShape, currentShape);

		// move actual association to later step
		parseOutgoings(jsonShape, currentShape, outgoingMap);
		parseChildShapes(jsonShape, currentShape, childMap);

		// has been moved up to give enough hints for deciding whether it should
		// be Node or Edge
		// parseDockers(modelJSON, current);

		parseBounds(jsonShape, currentShape);
		if (currentShape instanceof GenericEdge) {
			parseTarget(jsonShape, (GenericEdge<S, D>) currentShape, targetMap);
		}
		parseLabels(jsonShape, currentShape);
		
		//set the diagram, because will cause inconsistencies if skipped!
		currentShape.setDiagram(diagram);
		
		return currentShape;
	}


	protected void parseLabels(JSONObject jsonShape, S currentShape) throws JSONException {
		if (jsonShape.has("labels")) {
			List<LabelSettings> labelSettings = new ArrayList<LabelSettings>();
			JSONArray labels = jsonShape.getJSONArray("labels");
			for (int i = 0; i < labels.length(); i++) {
				JSONObject jsonLabel = labels.getJSONObject(i);
				LabelSettings label = new LabelSettings();

				if (jsonLabel.has("x") && jsonLabel.has("y"))
					label.setPosition(new Point(jsonLabel.getDouble("x"), jsonLabel.getDouble("y")));
				if (jsonLabel.has("distance"))
					label.setDistance((float) jsonLabel.getDouble("distance"));
				if (jsonLabel.has("ref"))
					label.setReference(jsonLabel.getString("ref"));
				if (jsonLabel.has("from"))
					label.setFrom(jsonLabel.getInt("from"));
				if (jsonLabel.has("to"))
					label.setTo(jsonLabel.getInt("to"));
				if (jsonLabel.has("align"))
					label.setAlignHorizontal(HorizontalAlign.fromString(jsonLabel.getString("align")));
				if (jsonLabel.has("valign"))
					label.setAlignVertical(VerticalAlign.fromString(jsonLabel.getString("valign")));
				if (jsonLabel.has("edge"))
					label.setEdgePos(EdgePosition.fromString(jsonLabel.getString("edge")));
				if (jsonLabel.has("orientation"))
					label.setOrientation(LabelOrientation.fromString(jsonLabel.getString("orientation")));

				Anchors anchors = new Anchors();
				if (jsonLabel.has("top") && jsonLabel.getBoolean("top"))
					anchors.addAnchor(Anchor.TOP);
				if (jsonLabel.has("right") && jsonLabel.getBoolean("right"))
					anchors.addAnchor(Anchor.RIGHT);
				if (jsonLabel.has("bottom") && jsonLabel.getBoolean("bottom"))
					anchors.addAnchor(Anchor.BOTTOM);
				if (jsonLabel.has("left") && jsonLabel.getBoolean("left"))
					anchors.addAnchor(Anchor.LEFT);
				label.setAnchors(anchors);

				parseLabelStyles(jsonLabel, label);
				labelSettings.add(label);
			}
			currentShape.setLabelSettings(labelSettings);
		}
	}


	protected void parseLabelStyles(JSONObject jsonLabel, LabelSettings labelSetting) throws JSONException {
		if (jsonLabel.has("styles")) {
			JSONObject jsonStyles = jsonLabel.getJSONObject("styles");
			// create a new style object and fill it
			LabelStyle style = new LabelStyle();
			style.setFontFamily(jsonStyles.optString("family", null));
			if (jsonStyles.has("size")) {
				double size = jsonStyles.optDouble("size", Double.NaN);
				if (size == Double.NaN)
					throw new IllegalArgumentException("Invalid size value: " + jsonStyles.toString());
				else
					style.setFontSize(size);
			}

			style.setBold(jsonStyles.optBoolean("bold", false));

			style.setItalic(jsonStyles.optBoolean("italic", false));

			if (jsonStyles.has("fill")) {
				try {
					style.setFill(Color.decode(jsonStyles.getString("fill")));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Label fill color could not be decoded: "
							+ jsonStyles.toString(), e);
				}
			}
			// set the style object
			labelSetting.setStyle(style);
		}
	}


	/**
	 * parse the stencil out of a JSONObject and set it to the current shape
	 * 
	 * @param jsonShape
	 * @param currentShape
	 * @throws JSONException
	 */
	protected void parseStencil(JSONObject jsonShape, S currentShape)//, StencilSetReference stencilsetRef)
			throws JSONException {
		// get stencil id
		if (jsonShape.has("stencil")) {
			JSONObject stencil = jsonShape.getJSONObject("stencil");
			String stencilString = "";
			if (stencil.has("id") && !stencil.getString("id").trim().equals("")) {
				stencilString = stencil.getString("id");
			} else {
				throw new IllegalArgumentException("No id found for stencil");
			}
			currentShape.setStencilId(stencilString);
		}
	}


	/**
	 * crates a StencilSet object and add it to the current diagram
	 * 
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	protected void parseStencilSet(JSONObject modelJSON, D current) throws JSONException {
		// get stencil type
		if (modelJSON.has("stencilset")) {
			JSONObject object = modelJSON.getJSONObject("stencilset");
			StencilSetReference ssRef;

			if (object.has("namespace") && !object.getString("namespace").trim().equals(""))
				ssRef = new StencilSetReference(object.getString("namespace"));
			else
				throw new IllegalArgumentException("No namespace found for stencil set");

			if (object.has("url"))
				ssRef.setUrl(object.getString("url"));

			current.setStencilsetRef(ssRef);
		}
	}


	/**
	 * Adds all JSON properties to the current shape. Preserves the data types of properties as found in the JSON.
	 * 
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	protected void parseProperties(JSONObject modelJSON, S current)
			throws JSONException {
		if (modelJSON.has("properties")) {
			JSONObject propsObject = modelJSON.getJSONObject("properties");
			Iterator<String> keys = propsObject.keys();

			while (keys.hasNext()) {
				String key = keys.next();
				Object value = propsObject.get(key);

				current.setProperty(key, value);
			}
		}
	}


	/**
	 * adds all json extension to an diagram
	 * 
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	protected void parseSsextensions(JSONObject modelJSON, D current) throws JSONException {
		if (modelJSON.has("ssextensions")) {
			JSONArray array = modelJSON.getJSONArray("ssextensions");
			for (int i = 0; i < array.length(); i++) {
				current.addSsextension(array.getString(i));
			}
		}
	}


	/**
	 * Parse the outgoings of a json object and update the outgoingList, to be able to add all shape references to the
	 * current shape later.
	 * 
	 * @param jsonShape
	 * @param currentShape
	 * @param outgoingMap
	 * @throws JSONException
	 */
	protected void parseOutgoings(JSONObject jsonShape, S currentShape, Map<String, List<String>> outgoingMap)
			throws JSONException {
		if (jsonShape.has("outgoing")) {
			JSONArray outgoingsArray = jsonShape.getJSONArray("outgoing");

			List<String> outgoingsList = new ArrayList<String>(); // outgoingMap.get(current.getResourceId());

			for (int i = 0; i < outgoingsArray.length(); i++) {
				outgoingsList.add(outgoingsArray.getJSONObject(i).getString("resourceId"));
			}

			if (outgoingsList.size() > 0)
				outgoingMap.put(currentShape.getResourceId(), outgoingsList);
		}
	}


	/**
	 * creates a shape list containing all child shapes and set it to the current shape new shape get added to the shape
	 * array
	 * 
	 * @param jsonShape
	 * @param currentShape
	 * @throws JSONException
	 */
	protected void parseChildShapes(JSONObject jsonShape, S currentShape,
			Map<String, List<String>> childMap) throws JSONException {
		if (jsonShape.has("childShapes")) {
			List<String> childShapes = new ArrayList<String>();
			JSONArray childShapeObject = jsonShape.getJSONArray("childShapes");
			
			for (int i = 0; i < childShapeObject.length(); i++) {
				childShapes.add(childShapeObject.getJSONObject(i).getString("resourceId"));
			}
			if (childShapes.size() > 0) {
				childMap.put(currentShape.getResourceId(), childShapes);
			}
		}
	}


	/**
	 * creates a point array of all dockers and add it to the current shape
	 * 
	 * @param modelJSON
	 * @param shapeId
	 * @throws JSONException
	 */
	protected List<Point> getDockers(JSONObject modelJSON, String shapeId) throws JSONException {
		List<Point> dockers = new ArrayList<Point>();
		if (modelJSON.has("dockers")) {
			JSONArray dockersObject = modelJSON.getJSONArray("dockers");
			for (int i = 0; i < dockersObject.length(); i++) {
				JSONObject docker = dockersObject.getJSONObject(i);
				Double x, y;
				if (isPropertyUndefined(docker, "x")) {
					LOGGER.warn("Couldn't parse docker coordinate, " + "setting to 'null' (id='" + shapeId + "'");
					x = null;
				} else {
					x = dockersObject.getJSONObject(i).getDouble("x");
				}
				if (isPropertyUndefined(docker, "y")) {
					LOGGER.warn("Couldn't parse docker coordinate, " + "setting to 'null' (id='" + shapeId + "'");
					y = null;
				} else {
					y = dockersObject.getJSONObject(i).getDouble("y");
				}
				if (y == null || x == null)
					dockers.add(null);
				else
					dockers.add(new Point(x, y));

			}
		}
		return dockers;
	}


	/**
	 * creates a bounds object with both point parsed from the json and set it to the current shape
	 * 
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	protected void parseBounds(JSONObject modelJSON, S current) throws JSONException {
		if (modelJSON.has("bounds")) {
			JSONObject boundsObject = modelJSON.getJSONObject("bounds");
			try {
				current.setBounds(new Bounds(new Point(boundsObject.getJSONObject("lowerRight").getDouble("x"),
						boundsObject.getJSONObject("lowerRight").getDouble("y")), new Point(boundsObject.getJSONObject(
						"upperLeft").getDouble("x"), boundsObject.getJSONObject("upperLeft").getDouble("y"))));
			} catch (JSONException e) {
				LOGGER.warn("Couldn't parse bounds, " + "setting to 'null' (id='" + current.getResourceId() + "'");
				current.setBounds(null);
			}
		}
	}


	/**
	 * Parse the target resource and update targetMap to be able to add it to the current shape later.
	 * 
	 * @param jsonShape
	 * @param currentEdge
	 * @param targetMap
	 * @throws JSONException
	 */
	protected void parseTarget(JSONObject jsonShape, GenericEdge<S, D> currentEdge,
			Map<String, String> targetMap) throws JSONException {
		if (jsonShape.has("target")) {
			JSONObject targetObject = jsonShape.getJSONObject("target");
			if (targetObject.has("resourceId")) {
				targetMap.put(currentEdge.getResourceId(), targetObject.getString("resourceId"));
			}
		}
	}


	/**
	 * Prepare a model JSON for analyze, resolves the hierarchical structure creates a HashMap which contains all
	 * resourceIds as keys and for each key the JSONObject, all id are keys of this map
	 * 
	 * @param object
	 * @return a map; keys: all ressourceIds; values: all child JSONObjects
	 * @throws JSONException
	 */
	protected Map<String, JSONObject> flatRessources(JSONObject object) throws JSONException {
		Map<String, JSONObject> result = new HashMap<String, JSONObject>();

		// no cycle in hierarchies!!
		if (object.has("resourceId") && object.has("childShapes")) {
			if (result.put(object.getString("resourceId"), object) != null) {
				/*
				 * if result of put is not null, then another shape with the same id exists or there is a cyclic
				 * dependency!
				 */
				throw new JSONException("Discovered duplicate id or cyclic dependency for resourceId '"
						+ object.getString("resourceId") + "'");
			}
			JSONArray childShapes = object.getJSONArray("childShapes");
			for (int i = 0; i < childShapes.length(); i++) {
				result.putAll(flatRessources(childShapes.getJSONObject(i)));
			}

		}

		return result;
	}


	protected boolean isPropertyUndefined(JSONObject object, String attr) {
		if (!object.has(attr) || object.isNull(attr))
			return true;
		else {
			String attrString;
			try {
				attrString = object.getString(attr);
			} catch (JSONException e) {
				// can't happen, since we just tested for existence
				attrString = null;
			}
			attrString = (attrString == null) ? null : attrString.trim();
			return attrString == null || attrString.equals("") || attrString.equals("null")
					|| attrString.equals("undefined");
		}
	}


	public abstract D createNewDiagram(String resourceId);


	public abstract E createNewEdge(String resourceId);


	public abstract N createNewNode(String resourceId);


	public S createNewShapeOfCorrectType(String resourceId, List<Point> dockers) {
		S shape;
		if (GenericShapeImpl.isEdge(dockers))
			shape = (S) createNewEdge(resourceId);
		else
			shape = (S) createNewNode(resourceId);
		shape.setDockers(dockers);
		return shape;
	}
}
