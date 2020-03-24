/* 
 * $Id: MultiLineVertexView.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved.
 * 
 * See LICENSE file for license details. If you are unable to locate
 * this file please contact info (at) jgraph (dot) com.
 */
package org.apromore.jgraph.components.labels;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apromore.jgraph.graph.CellViewRenderer;
import org.apromore.jgraph.graph.GraphCellEditor;
import org.apromore.jgraph.graph.VertexView;

/**
 * Vertex view that supports {@link JGraphpadBusinessObject} rendering and
 * in-place editing, that means it supports simple text, rich text and component
 * values.
 */
public class MultiLineVertexView extends VertexView {

	/**
	 * Holds the static editor for views of this kind.
	 */
	public static RichTextEditor editor = new RichTextEditor();

	/**
	 * Holds the static editor for views of this kind.
	 */
	public static RedirectingEditor redirector = new RedirectingEditor();

	/**
	 * Holds the static renderer for views of this kind.
	 */
	public static MultiLineVertexRenderer renderer = new MultiLineVertexRenderer();

	/**
	 * Empty constructor for persistence.
	 */
	public MultiLineVertexView() {
		super();
	}

	/**
	 * Constructs a new vertex view for the specified cell.
	 * 
	 * @param cell
	 *            The cell to construct the vertex view for.
	 */
	public MultiLineVertexView(Object cell) {
		super(cell);
	}

	/**
	 * Returns {@link #editor} if the user object of the cell is a rich text
	 * value or {@link #redirector} if the user object is a component.
	 * 
	 * @return Returns the editor for the cell view.
	 */
	public GraphCellEditor getEditor() {
		Object value = ((DefaultMutableTreeNode) getCell()).getUserObject();
		if (value instanceof RichTextBusinessObject) {
			RichTextBusinessObject obj = (RichTextBusinessObject) value;
			if (obj.isRichText())
				return editor;
			else if (obj.isComponent())
				return redirector;
		}
		return super.getEditor();
	}

	/**
	 * Returns the {@link #renderer}.
	 * 
	 * @return Returns the renderer for the cell view.
	 */
	public CellViewRenderer getRenderer() {
		return renderer;
	}
}
