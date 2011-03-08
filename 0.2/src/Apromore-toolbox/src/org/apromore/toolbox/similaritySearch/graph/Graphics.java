package org.apromore.toolbox.similaritySearch.graph;

public class Graphics {

	public Graphics(int height, int width, int x, int y) {
		super();
		this.height = height;
		this.width = width;
		this.x = x;
		this.y = y;
	}
	
	private int height;
	private int width;
	private int x;
	private int y;
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
}
