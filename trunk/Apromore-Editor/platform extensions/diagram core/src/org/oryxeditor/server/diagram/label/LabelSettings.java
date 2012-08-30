package org.oryxeditor.server.diagram.label;

import java.util.HashMap;
import java.util.Map;

import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.label.Anchors.Anchor;
import org.oryxeditor.server.diagram.util.NumberUtil;


/**
 * Contains information on how to position a certain label and the label's style
 * There are several modes of positioning, each setting/reading a different subset of the data:
 * <ul>
 * 	<li> absolute position (position)
 * 			the text is positioned at the coordinates given by {@link #getPosition()}
 * 	<li> edge position (edge position)
 * 			the text is positioned at the relative position along an edge given by {@link #getEdgePos()}
 * 	<li> reference point (position, orientation, distance, from, to)
 * 			the text is positioned at the point that satisfies the following criteria:
 * 			<ul>
 * 				<li> its on a vector thats orthogonal to the edge segment defined by {@link #getFrom()} and {@link #getTo()}
 * 				<li> the base point of that vector is at the coordinates given by {@link #getPosition()}
 * 				<li> the length of that vector equals {@link #getDistance()}
 * 				<li> the end point of the vector points to the corner of the text's bounding box as defined by {@link #getOrientation()}
 * 			</ul>
 * </ul>
 * 
 * The rest of the data not mentioned above is applied independently of the positioning mode
 * 
 * @author philipp.maschke
 *
 */
public class LabelSettings {

	private HorizontalAlign alignHorizontal;
	private VerticalAlign alignVertical;
	private Anchors anchors;
	private EdgePosition edgePos;
	private LabelOrientation orientation;
	private Point position;
	private Float distance;
	private String reference;
	private Integer from;
	private Integer to;
	private LabelStyle style;
	
	public LabelSettings(){
		
	}
	
	public LabelSettings(Map<String, String> labelMap){
		this();
		Double double1 = NumberUtil.createDouble(labelMap.get("x"));
		Double double2 = NumberUtil.createDouble(labelMap.get("y"));
		if (double1 != null && double2 != null)
			position = new Point(double1, double2);
		
		distance = NumberUtil.createFloat(labelMap.get("distance"));
		reference = labelMap.get("ref");
		from = NumberUtil.createInt(labelMap.get("from"));
		to = NumberUtil.createInt(labelMap.get("to"));
		if (labelMap.get("align") != null)
		alignHorizontal = HorizontalAlign.fromString(labelMap.get("align"));
		if (labelMap.get("valign") != null)
		alignVertical = VerticalAlign.fromString(labelMap.get("valign"));
		if (labelMap.get("edge") != null)
		edgePos = EdgePosition.fromString(labelMap.get("edge"));
		if (labelMap.get("orientation") != null)
		orientation = LabelOrientation.fromString(labelMap.get("orientation"));

		anchors = new Anchors();
		if (Boolean.getBoolean(labelMap.get("top")))
			anchors.addAnchor(Anchor.TOP);
		if (Boolean.getBoolean(labelMap.get("right")))
			anchors.addAnchor(Anchor.RIGHT);
		if (Boolean.getBoolean(labelMap.get("bottom")))
			anchors.addAnchor(Anchor.BOTTOM);
		if (Boolean.getBoolean(labelMap.get("left")))
			anchors.addAnchor(Anchor.LEFT);
		
		//TODO style
	}
	
	
	/**
	 * Horizontal alignment of the text, relative to its position
	 * @return
	 */
	public HorizontalAlign getAlignHorizontal() {
		return alignHorizontal;
	}
	/**
	 * Horizontal alignment of the text, relative to its position
	 * @param alignHorizontal
	 */
	public void setAlignHorizontal(HorizontalAlign alignHorizontal) {
		this.alignHorizontal = alignHorizontal;
	}
	
	/**
	 * Vertical alignment of the text, relative to its position
	 * @return
	 */
	public VerticalAlign getAlignVertical() {
		return alignVertical;
	}
	/**
	 * Vertical alignment of the text, relative to its position
	 * @param alignVertical
	 */
	public void setAlignVertical(VerticalAlign alignVertical) {
		this.alignVertical = alignVertical;
	}
	
	/**
	 * Layout anchors that determine to which side(s) of the parent shape the label will have a fixed distance
	 * @return
	 */
	public Anchors getAnchors() {
		return anchors;
	}
	/**
	 * Layout anchors that determine to which side(s) of the parent shape the label will have a fixed distance
	 * @param anchors
	 */
	public void setAnchors(Anchors anchors) {
		this.anchors = anchors;
	}
	
	/**
	 * General position along an edge that this label should have
	 * @return
	 */
	public EdgePosition getEdgePos() {
		return edgePos;
	}
	/**
	 * General position along an edge that this label should have
	 * @param edgePos
	 */
	public void setEdgePos(EdgePosition edgePos) {
		this.edgePos = edgePos;
	}
	
	/**
	 * Which corner of the label should be placed at the reference point
	 * @return
	 */
	public LabelOrientation getOrientation() {
		return orientation;
	}
	/**
	 * Which corner of the label should be placed at the reference point
	 * @param orientation
	 */
	public void setOrientation(LabelOrientation orientation) {
		this.orientation = orientation;
	}
	
	/**
	 * Depending on the mode of positioning this is either the final text position or 
	 * the reference point along the edge that this label belongs to
	 * @return
	 */
	public Point getPosition() {
		return position;
	}
	/**
	 * Depending on the mode of positioning this is either the final text position or 
	 * the reference point along the edge that this label belongs to
	 * @param position
	 */
	public void setPosition(Point position) {
		this.position = position;
	}
	
	/**
	 * Distance between the reference point and the actual text position
	 * @return
	 */
	public Float getDistance() {
		return distance;
	}
	/**
	 * Distance between the reference point and the actual text position
	 * @param distance
	 */
	public void setDistance(Float distance) {
		this.distance = distance;
	}
	
	/**
	 * Id of the label that should be repositioned
	 * @return
	 */
	public String getReference() {
		return reference;
	}
	/**
	 * Id of the label that should be repositioned
	 * @param reference
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	/**
	 * Index of the edge docker, which marks the beginning of the segment where the reference point is situated
	 * @return
	 */
	public Integer getFrom() {
		return from;
	}
	/**
	 * Index of the edge docker, which marks the beginning of the segment where the reference point is situated
	 * @param from
	 */
	public void setFrom(Integer from) {
		this.from = from;
	}
	
	/**
	 * Index of the edge docker, which marks the end of the segment where the reference point is situated
	 * @return
	 */
	public Integer getTo() {
		return to;
	}
	/**
	 * Index of the edge docker, which marks the end of the segment where the reference point is situated
	 * @param to
	 */
	public void setTo(Integer to) {
		this.to = to;
	}
	
	/**
	 * Style settings for this label
	 * @param style
	 */
	public void setStyle(LabelStyle style) {
		this.style = style;
	}
	/**
	 * Style settings for this label
	 * @return
	 */
	public LabelStyle getStyle() {
		return style;
	}
	
	
	/**
	 * Whether there are any settings for positioning. Returns true if at least one of the following is not null:
	 * <ul>
	 * <li> position
	 * <li> orientation
	 * <li> edge position
	 * <li> distance
	 * <li> from
	 * <li> to
	 * </ul>
	 * 
	 * @return
	 */
	public boolean hasPositioningInfo(){
		return getDistance() != null || getEdgePos() != null || getFrom() != null || 
			getOrientation() != null || getPosition() != null || getTo() != null;
	}
	
	
	public Map<String,String> getSettingsMap(){
		Map<String,String> map = new HashMap<String, String>();
		
		if (position != null){
			map.put("x", position.getX().toString());
			map.put("y", position.getY().toString());
		}
		if (distance != null)
			map.put("distance", distance.toString());
		
		if (reference != null)
		map.put("ref", reference);
		
		if (from != null)
		map.put("from", from.toString());
		
		if (to != null)
		map.put("to", to.toString());
		
		if (alignHorizontal != null)
		map.put("align", alignHorizontal.toString());
		
		if (alignVertical != null)
		map.put("valign", alignVertical.toString());
		
		if (edgePos != null)
		map.put("edge", edgePos.toString());
		
		if (orientation != null)
		map.put("orientation", orientation.toString());
		
		if (anchors != null)
		map.put("left", String.valueOf(anchors.contains(Anchor.LEFT)));
		map.put("top", String.valueOf(anchors.contains(Anchor.TOP)));
		map.put("right", String.valueOf(anchors.contains(Anchor.RIGHT)));
		map.put("bottom", String.valueOf(anchors.contains(Anchor.BOTTOM)));
		
		return map;
	}
}
