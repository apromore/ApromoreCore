/*
 * Copyright (c) 2006, David Benson
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.simple;

import java.awt.geom.Rectangle2D;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.JGraphLayout;

/**
 * A simple grid layout algorithm that takes the specified vertices and
 * arranges them in rectangular fashion
 */
public class SimpleGridLayout implements JGraphLayout {

	/** Limit of the number of vertices */
	protected int numCellsPerRow = 0;
	
	/** Height spacing between vertices */
	protected int heightSpacing = 20;
	
	/** Width spacing between vertices */
	protected int widthSpacing = 20;
	
	/** Offset of the top left corner of the grid */
	protected int offsetX = widthSpacing;
	
	/** Offset of the top left corner of the grid */
	protected int offsetY = heightSpacing;

	/** Whether or not to only act of unconnected vertices */
	protected boolean actOnUnconnectedVerticesOnly = true;
	
	/** Whether or not the vertices are to be ordered by their model ordering */
	protected boolean ordered = false;
	
	/**
	 * 
	 */
	public SimpleGridLayout() {
		
	}

	/**
	 * Executes the main layout
	 */
	public void run(JGraphFacade graph) {
		Object[] vertices = null;
		if (actOnUnconnectedVerticesOnly) {
			vertices = graph.getUnconnectedVertices(ordered).toArray();
		} else {
			vertices = graph.getVertices().toArray();
		}
		if (vertices == null || vertices.length == 0) {
			return;
		}
		int cellsPerRow = numCellsPerRow;
		// Work out the number of cells per row
		// Assuming as square shaped a grid as possible, unless the
		// number of cells per row has been set to something non-zero
		if (numCellsPerRow == 0) {
			cellsPerRow = (int)Math.sqrt(vertices.length);
		}
		if (cellsPerRow == 0) {
			return;
		}
		// Determine the widest and heighest vertices
		double maxWidth = 0;
		double maxHeight = 0;
		for (int i =  0; i < vertices.length; i++) {
			Rectangle2D cellBounds = graph.getBounds(vertices[i]);
			if (cellBounds.getWidth() > maxWidth) {
				maxWidth = cellBounds.getWidth();
			}
			if (cellBounds.getHeight() > maxHeight) {
				maxHeight = cellBounds.getHeight();
			}
		}
		// Lay out the cells in a grid
		int numVertices = vertices.length;
		int currentX = offsetX;
		int currentY = offsetY;
		int numRows = numVertices/cellsPerRow;
		if (numVertices%cellsPerRow > 0) {
			numRows++;
		}
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < cellsPerRow; j++) {
				// Need to check for row short of the full number of vertices
				if (j + i*cellsPerRow >= numVertices) {
					break;
				}
				graph.setLocation(vertices[j + i*cellsPerRow], currentX, currentY);
				currentX += maxWidth + widthSpacing;
			}
			currentX = widthSpacing;
			currentY += maxHeight + heightSpacing;
		}
	}

	/**
	 * @return the numCellsPerRow
	 */
	public int getNumCellsPerRow() {
		return numCellsPerRow;
	}

	/**
	 * @param numCellsPerRow the numCellsPerRow to set
	 */
	public void setNumCellsPerRow(int numCellsPerRow) {
		this.numCellsPerRow = numCellsPerRow;
	}

	/**
	 * @return the actOnUnconnectedVerticesOnly
	 */
	public boolean isActOnUnconnectedVerticesOnly() {
		return actOnUnconnectedVerticesOnly;
	}

	/**
	 * @param actOnUnconnectedVerticesOnly the actOnUnconnectedVerticesOnly to set
	 */
	public void setActOnUnconnectedVerticesOnly(boolean actOnUnconnectedVerticesOnly) {
		this.actOnUnconnectedVerticesOnly = actOnUnconnectedVerticesOnly;
	}

	/**
	 * @return the heightSpacing
	 */
	public int getHeightSpacing() {
		return heightSpacing;
	}

	/**
	 * @param heightSpacing the heightSpacing to set
	 */
	public void setHeightSpacing(int heightSpacing) {
		this.heightSpacing = heightSpacing;
	}

	/**
	 * @return the widthSpacing
	 */
	public int getWidthSpacing() {
		return widthSpacing;
	}

	/**
	 * @param widthSpacing the widthSpacing to set
	 */
	public void setWidthSpacing(int widthSpacing) {
		this.widthSpacing = widthSpacing;
	}

	/**
	 * @return the ordered
	 */
	public boolean isOrdered() {
		return ordered;
	}

	/**
	 * @param ordered the ordered to set
	 */
	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}

	/**
	 * @return the offsetX
	 */
	public int getOffsetX() {
		return offsetX;
	}

	/**
	 * @param offsetX the offsetX to set
	 */
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	/**
	 * @return the offsetY
	 */
	public int getOffsetY() {
		return offsetY;
	}

	/**
	 * @param offsetY the offsetY to set
	 */
	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}
}
