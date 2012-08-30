package org.oryxeditor.server.diagram;

import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.util.NumberUtil;


/**
 * Represents a 2D Point on a canvas. Uses double precision coordinates
 * 
 * @author Philipp Maschke
 */
public 	class Point{
	Double x;
	Double y;
	
	/** 
	 * Creates a new Point with x and y coordinates
	 * @param x
	 * @param y
	 */
	public Point(Number x, Number y) {
		if (x == null) x = 0;
		if (y == null) y = 0;
		
		this.set(x.doubleValue(), y.doubleValue());
	}
	
	
	/** 
	 * Creates a new Point with the coordinates of the given point
	 * @param point
	 */
	public Point(Point point) {
		this.set(point);
	}
	
	/**
	 * Creates a new point with a json like stucture of the x/y coordinates.
	 * @param point
	 * @throws JSONException 
	 */
	public Point(String point) throws JSONException{
		this.set(point);
	}
	
	/**
	 * Creates a new point with a json of the x/y coordinates.
	 * @param point
	 * @throws JSONException 
	 */
	public Point(JSONObject point) throws JSONException{
		this.set(point);
	}
	
	/**
	 * Move the point relative to the coordinates
	 * @param x
	 * @param y
	 */
	public void moveBy(double x, double y){
		this.add(x, y);
	}
	
	/**
	 * Move the point relative to the point
	 * @param point
	 */
	public void moveBy(Point point){
		this.add(point);
	}
	
	/**
	 * Move the point absolute to the coordinates
	 * @param x
	 * @param y
	 */
	public void moveTo(double x, double y){
		this.set(x, y);
	}
	
	/**
	 * Move the point relative to the point
	 * @param point
	 */
	public void moveTo(Point point){
		this.set(point);
	}
	
	/**
	 * Move the point absolute to the coordinates
	 * @param x
	 * @param y
	 */
	public void set(double x, double y){
		this.setX(x);
		this.setY(y);
	}
	
	/**
	 * Move the point absolut to the point
	 * @param point
	 */
	public void set(Point point){
		this.set(point.getX(), point.getY());
	}
	
	/**
	 * Set the point regarding the coordinates in the json like structure
	 * @param point
	 * @throws JSONException 
	 */
	public void set(String point) throws JSONException{
		this.set(new JSONObject(point));
	}
	
	/**
	 * Set the point regarding the coordinates in the json
	 * @param point
	 * @throws JSONException 
	 */
	public void set(JSONObject point) throws JSONException{
		this.set(point.getDouble("x"), point.getDouble("y"));
	}
	
	/**
	 * @return the x
	 */
	public Double getX() {
		return x;
	}
	
	/**
	 * @param x the x to set
	 */
	public void setX(Double x) {
		this.x = x;
	}
	
	/**
	 * @return the y
	 */
	public Double getY() {
		return y;
	}
	
	/**
	 * @param y the y to set
	 */
	public void setY(Double y) {
		this.y = y;
	}
	
	/**
	 * Clones the point and returns a new one
	 */
	public Point copy(){
		return new Point(this);
	}
	
	public void subtract(double x, double y){
		this.x -= x;
		this.y -= y;
	}
	
	public void subtract(Point point){
		this.subtract(point.getX(), point.getY());
	}

	public void subtract(double a){
		this.subtract(a, a);
	}
	
	
	public void add(double x, double y){
		this.x += x;
		this.y += y;
	}
	
	public void add(Point point){
		this.add(point.getX(), point.getY());
	}

	public void add(double a){
		this.add(a, a);
	}
	
	public void multiply(double x, double y){
		this.x *= x;
		this.y *= y;
	}
	
	public void multiply(Point point){
		this.multiply(point.getX(), point.getY());
	}
	
	public void multiply(double a){
		this.multiply(a, a);
	}
	
	public void divide(double x, double y){
		if (x == 0 || y == 0){
			throw new IllegalArgumentException("Attempted division with 0!");
		}
		this.x /= x;
		this.y /= y;
	}
	
	public void divide(Point point){
		this.divide(point.getX(), point.getY());
	}
	
	public void divide(double a){
		this.divide(a, a);
	}
	
	/**
	 * Return a json representation of the point.
	 * @return
	 * @throws JSONException 
	 */
	public JSONObject toJSON() throws JSONException{
		JSONObject pos = new JSONObject();
		pos.put("x", this.getX());
		pos.put("y", this.getY());
		return pos;
	}
	
	/**
	 * Return a string representation of the point
	 * in a json like structure.
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
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}
	/**
	 * Checks for object equality first. 
	 * If other object is a point then returns true if their coordinates 
	 * are numerically equal (using {@link Double#compareTo(Double)}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (x.compareTo(other.x) != 0)
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (y.compareTo(other.y) != 0)
			return false;
		return true;
	}
	
	
	/**
	 * 
	 * @param other the other point
	 * @return whether both coordinates of the two points are same 
	 * (allowing for a minimal difference; see {@link NumberUtil#areNumbersSame(Number, Number)})
	 */
	public boolean hasSameCoordinatesAs(Point other){
		return NumberUtil.areNumbersSame(getX(), other.getX()) &&
			NumberUtil.areNumbersSame(getY(), other.getY());
	}
	
	
	/**
	 * 
	 * @param other the other point
	 * @param maxDelta maximum allowed difference per coordinate
	 * @return whether both coordinates of the two points are same (allowing for the given delta)
	 */
	public boolean hasSameCoordinatesAs(Point other, double maxDelta){
		return NumberUtil.areNumbersWithinDelta(getX(), other.getX(), maxDelta) &&
			NumberUtil.areNumbersWithinDelta(getY(), other.getY(), maxDelta);
	}
	
	/**
	 * Calculates the sum of two Points
	 * 
	 * @param p1
	 * @param p2
	 * @return the sum of p1 and p2 in a new Point object
	 */
	public static Point getSum(Point p1, Point p2) {
		Point result = new Point(p1);
		result.add(p2);
		return result;
	}
	
	/**
	 * Calculates the difference of two Points
	 * 
	 * @param p1
	 * @param p2
	 * @return the difference of p1 and p2 in a new Point object
	 */
	public static Point getDifference(Point p1, Point p2) {
		Point result = new Point(p1);
		result.subtract(p2);
		return result;
	}
	
	/**
	 * Calculates the product of two Points
	 * 
	 * @param p1
	 * @param p2
	 * @return the product of p1 and p2 in a new Point object
	 */
	public static Point getProduct(Point p1, Point p2) {
		Point result = new Point(p1);
		result.multiply(p2);
		return result;
	}

	/**
	 * Calculates the quotient of two Points
	 * 
	 * @param p1
	 * @param p2
	 * @return the quotient of p1 and p2 in a new Point object
	 */
	public static Point getQuotient(Point p1, Point p2) {
		Point result = new Point(p1);
		result.divide(p2);
		return result;
	}
}
