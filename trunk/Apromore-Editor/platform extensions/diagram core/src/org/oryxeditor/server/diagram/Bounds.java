package org.oryxeditor.server.diagram;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a rectangular space within a canvas.
 * Defined by the upper left and lower right points
 * 
 * @author Philipp Maschke
 *	
 */
public class Bounds {
	Point lowerRight;
	Point upperLeft;
	
	/**
	 * Creates a bounds with 0,0 and 0,0 coordinates.
	 */
	public Bounds(){
		this(new Point(0.0, 0.0), new Point(0.0, 0.0));
	}
	
	/**
	 * Creates the bounds with a json like structured string.
	 * @param string
	 */
	public Bounds(String string){
		try {
			JSONObject points = new JSONObject(string);
			this.upperLeft = new Point(points.getJSONObject("a"));
			this.lowerRight = new Point(points.getJSONObject("b"));
			validatePoints();
		} catch (JSONException e) {
			this.lowerRight = new Point(0.0, 0.0);
			this.upperLeft = new Point(0.0, 0.0);
		}
	}
	
	/**
	 * Creates the bounds with the json representation of it.
	 * @param points
	 */
	public Bounds(JSONObject points){
		try {
			this.upperLeft = new Point(points.getJSONObject("a"));
			this.lowerRight = new Point(points.getJSONObject("b"));
			validatePoints();
		} catch (JSONException e) {
			this.lowerRight = new Point(0.0, 0.0);
			this.upperLeft = new Point(0.0, 0.0);
		}
	}
	
	/**
	 * Constructs a Bounds defined by the two given points.<p/>
	 * Constructs the rectangle which is spanned by the two points and then computes coordinates for upper left and lower right points.<br/>
	 * This means, that the given points can be any two opposing corner points of the desired bounds
	 *  (they don't have to be upper left and lower right and they don't have to be in any order)
	 * @param first a corner point of the desired bounds
	 * @param second the opposing corner point of the desired bounds
	 */
	public Bounds(Point first, Point second) {
		this.upperLeft = first;
		this.lowerRight = second;
		validatePoints();
	}
	
	/**
	 * Returns a copy of the lower right point
	 */
	public Point getLowerRight() {
		return new Point(lowerRight);
	}
	
//	/**
//	 * Sets the new lower right. Also checks whether the new coordinates are valid 
//	 * (upper left coordinates really in upper left; lower right coordinates really in lower right).
//	 * <p/>
//	 * <b>CAUTION:</b> Automatically swaps coordinates, such that these constraints hold!
//	 * @param lowerRight the lowerRight to set
//	 */
//	public void setLowerRight(Point lowerRight) {
//		this.lowerRight = lowerRight;
//		validatePoints();
//	}
	
	/**
	 * Returns a copy of the upper left point
	 */
	public Point getUpperLeft() {
		return new Point(upperLeft);
	}
	
//	/**
//	 * Sets the new upper left. Also checks whether the new coordinates are valid 
//	 * (upper left coordinates really in upper left; lower right coordinates really in lower right).
//	 * <p/>
//	 * <b>CAUTION:</b> Automatically swaps coordinates, such that these constraints hold!
//	 * 
//	 * @param upperLeft the upperLeft to set
//	 */
//	public void setUpperLeft(Point upperLeft) {
//		this.upperLeft = upperLeft;
//		validatePoints();
//	}
	
	/**
	 * Updated the bounds with the new values. <p/>
	 * Computes the rectangle spanned by the two given points and infers the new coordinates of upper left and lower right points.<bt/>
	 * This means that the given points don't need to be in the "right" order and may even be the lower left and upper right coordinates.
	 * 
	 * @param first one corner point of the new bounds
	 * @param second corner point on the opposite side of first
	 */
	public void setCoordinates(Point first, Point second){
		this.upperLeft = first;
		this.lowerRight = second;
		validatePoints();
	}
	/**
	 * Updated the bounds with the new values. <p/>
	 * Computes the rectangle spanned by the two given points and infers the new coordinates of upper left and lower right points.<bt/>
	 * This means that the given points don't need to be in the "right" order and may even be the lower left and upper right coordinates.
	 * 
	 * @param firstX X-coordinate of one corner point of the new bounds
	 * @param firstY Y-coordinate of one corner point of the new bounds
	 * @param secondX X-coordinate of the corner point on the opposite side of first
	 * @param secondY Y-coordinate of the corner point on the opposite side of first
	 */
	public void setCoordinates(Number firstX, Number firstY, Number secondX, Number secondY){
		setCoordinates(new Point(firstX, firstY), new Point(secondX, secondY));
	}
	
	
	/**
	 * Move the bounds to the absolute point.
	 * @param a
	 */
	public void moveTo(Point a){
		Point size = new Point(this.getWidth(), this.getHeight());
		this.upperLeft.set(a);
		this.lowerRight.set(a);
		this.lowerRight.add(size);
	}
	
	/**
	 * Move the bounds relative.
	 * @param a
	 */
	public void moveBy(Point a){
		this.lowerRight.moveBy(a);
		this.upperLeft.moveBy(a);
	}

	/**
	 * Extend the lowerRight with the coordinates
	 * @param x
	 * @param y
	 */
	public void extend(double x, double y){
		this.lowerRight.add(x, y);
	}
	
	/**
	 * Extend the lowerRight with the point
	 * @param point
	 */
	public void extend(Point point){
		this.extend(point.getX(), point.getY());
	}
	
	/**
	 * Widen the bounds with the upperleft and lowerright
	 * @param x
	 * @param y
	 */
	public void widen(double x, double y){
		this.upperLeft.subtract(x, y);
		this.lowerRight.add(x, y);
	}
	
	/**
	 * Widen the bounds with the upperleft and lowerright
	 * @param point
	 */
	public void widen(Point point){
		this.widen(point.getX(), point.getY());
	}
	
	/**
	 * Returns the middle point, the point between upperleft and lowerright
	 * @return
	 */
	public Point getMiddle(){
		Point p = this.getLowerRight();
		p.subtract(this.getUpperLeft());
		p.divide(2);
		return p;
	}
	
	/**
	 * Return the center of the bounds
	 * @return
	 */
	public Point getCenter(){
		Point p = this.getMiddle();
		p.add(this.upperLeft);
		return p;
	}
	
	
	/**
	 * Returns a copy of the upper right point
	 */
	public Point getUpperRight(){
		return new Point(getLowerRight().getX(), getUpperLeft().getY());
	}
	
	
	/**
	 * Returns a copy of the lower left point
	 */
	public Point getLowerLeft(){
		return new Point(getUpperLeft().getX(), getLowerRight().getY());
	}
	
	
	/**
	 * Return the width
	 * @return
	 */
	public double getWidth(){
		return this.getLowerRight().getX() - this.getUpperLeft().getX();
	}

	/**
	 * Return the height
	 * @return
	 */
	public double getHeight(){
		return this.getLowerRight().getY() - this.getUpperLeft().getY();
	}
	
	
	/**
	 * Returns width and height of the bounds as a Point object
	 * @return
	 */
	public Point getSize(){
		return new Point(getWidth(), getHeight());
	}
	
	
	/**
	 * Return true if the coordinates is on or inside the bounds
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isPointIncluded(double x, double y){
		return this.upperLeft.getX() <= x && this.upperLeft.getY() <= y &&
				this.lowerRight.getX() >= x && this.lowerRight.getY() >= y;
	}
	
	/**
	 * Return true if the point is on or inside the bounds
	 * @param point
	 * @return
	 */
	public boolean isPointIncluded(Point point){
		if (point == null)
			return false;
		
		return this.isPointIncluded(point.getX(), point.getY());
	}
	
	/**
	 * Returns a new instance of the given bounds
	 */
	public Bounds copy(){
		return new Bounds(this.upperLeft.copy(), this.lowerRight.copy());
	}
	
	/**
	 * Returns a json representation of the bounds.
	 * @return
	 * @throws JSONException 
	 */
	public JSONObject toJSON() throws JSONException{
		JSONObject pos = new JSONObject();
		pos.put("a", this.getUpperLeft().toJSON());
		pos.put("b", this.getLowerRight().toJSON());
		return pos;
	}
	
	/**
	 * Returns a string representation of the bounds in 
	 * a json like structure.
	 * In case of a JSONException, it returns super.toString().
	 * @return
	 */
	public String toString(){
		try {
			return this.toJSON().toString();
		} catch (JSONException e) {
			return super.toString();
		}
	}
	
	
	/**
	 * Checks whether lower right and upper left of both bounds are the same, using {@link Point#hasSameCoordinatesAs(Point)}
	 * @param other
	 * @return true, if lower right and upper left of both bounds are the same
	 */
	public boolean hasSamePositionsAs(Bounds other){
		return upperLeft.hasSameCoordinatesAs(other.getUpperLeft()) && 
			lowerRight.hasSameCoordinatesAs(other.getLowerRight());
	}
	
	
	/**
	 * Checks whether lower right and upper left of both bounds are the same, using {@link Point#hasSameCoordinatesAs(Point, double)}
	 * @param other
	 * @param maxDelta the maximum allowed difference per coordinate
	 * @return true, if lower right and upper left of both bounds are the same (allowing for the given delta)
	 */
	public boolean hasSamePositionsAs(Bounds other, double maxDelta){
		return upperLeft.hasSameCoordinatesAs(other.getUpperLeft(), maxDelta) && 
			lowerRight.hasSameCoordinatesAs(other.getLowerRight(), maxDelta);
	}
	
	
	/**
	 * Computes the actual rectangle spanned by the two given points and determines the actual upper left and lower right points.
	 */
	private void validatePoints(){
		Point newLowerRight = new Point(Math.max(this.upperLeft.getX(), this.lowerRight.getX()), Math.max(this.upperLeft.getY(), this.lowerRight.getY()));
		Point newUpperLeft = new Point(Math.min(this.upperLeft.getX(), this.lowerRight.getX()), Math.min(this.upperLeft.getY(), this.lowerRight.getY()));
		upperLeft = newUpperLeft;
		lowerRight = newLowerRight;
	}

}
