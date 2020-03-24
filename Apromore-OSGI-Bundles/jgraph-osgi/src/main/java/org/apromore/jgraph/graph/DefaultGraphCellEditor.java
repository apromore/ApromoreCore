/*
 * @(#)DefaultGraphCellEditor.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.plaf.FontUIResource;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.event.GraphSelectionEvent;
import org.apromore.jgraph.event.GraphSelectionListener;

public class DefaultGraphCellEditor
	implements ActionListener, GraphCellEditor, GraphSelectionListener, Serializable {

	/** Editor handling the editing. */
	protected GraphCellEditor realEditor;

	/** Editing container, will contain the editorComponent. */
	protected Container editingContainer;

	/** Component used in editing, obtained from the editingContainer. */
	transient protected Component editingComponent;

	/**
	 * Internal Note, maybe isCellEditable return true.
	 * This is set in configure  based on the path being edited and the
	 * selected selected path.
	 */
	protected boolean canEdit;

	/** Used in editing. Indicates position to place editingComponent. */
	protected transient int offsetX;
	protected transient int offsetY;

	/** JTree instance listening too. */
	protected transient JGraph graph;

	/** last path that was selected. */
	protected transient Object lastCell;

	/** True if the border selection color should be drawn. */
	protected Color borderSelectionColor;

	/** Icon to use when editing. */
	protected transient Icon editingIcon;

	/** Font to paint with, null indicates font of renderer is to be used. */
	protected Font font;

	/**
	 * Constructs a DefaultTreeCellEditor object for a JGraph using the
	 * specified renderer and a default editor. (Use this constructor
	 * for normal editing.)
	 */
	public DefaultGraphCellEditor() {
		this(null);
	}

	/**
	 * Constructs a DefaultTreeCellEditor object for a JTree using the
	 * specified renderer and the specified editor. (Use this constructor
	 * for specialized editing.)
	 *
	 * @param editor    a TreeCellEditor object
	 */
	public DefaultGraphCellEditor(GraphCellEditor editor) {
		realEditor = editor;
		if (realEditor == null)
			realEditor = createGraphCellEditor();
		editingContainer = createContainer();
		setBorderSelectionColor(
			UIManager.getColor("Tree.editorBorderSelectionColor"));
	}

	/**
	  * Sets the color to use for the border.
	  */
	public void setBorderSelectionColor(Color newColor) {
		borderSelectionColor = newColor;
	}

	/**
	  * Returns the color the border is drawn.
	  */
	public Color getBorderSelectionColor() {
		return borderSelectionColor;
	}

	/**
	 * Sets the font to edit with. <code>null</code> indicates the renderers
	 * font should be used. This will NOT override any font you have set in
	 * the editor the receiver was instantied with. If null for an editor was
	 * passed in a default editor will be created that will pick up this font.
	 *
	 * @param font  the editing Font
	 * @see #getFont
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * Gets the font used for editing.
	 *
	 * @return the editing Font
	 * @see #setFont
	 */
	public Font getFont() {
		return font;
	}

	//
	// TreeCellEditor
	//

	/**
	 * Configures the editor.  Passed onto the realEditor.
	 */
	public Component getGraphCellEditorComponent(
		JGraph graph,
		Object cell,
		boolean isSelected) {

		setGraph(graph);

		editingComponent =
			realEditor.getGraphCellEditorComponent(graph, cell, isSelected);

		determineOffset(graph, cell, isSelected);

		canEdit = (lastCell != null && cell != null && lastCell.equals(cell));

		CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
		if (view != null)
			setFont(GraphConstants.getFont(view.getAllAttributes()));
		editingContainer.setFont(font);
		
		return editingContainer;
	}

	/**
	 * Returns the value currently being edited.
	 */
	public Object getCellEditorValue() {
		return realEditor.getCellEditorValue();
	}

	/**
	 * If the realEditor returns true to this message, prepareForEditing
	 * is messaged and true is returned.
	 */
	public boolean isCellEditable(EventObject event) {
		boolean retValue = false;

		if (!realEditor.isCellEditable(event))
			return false;
		if (canEditImmediately(event))
			retValue = true;
		if (retValue)
			prepareForEditing();
		return retValue;
	}

	/**
	 * Messages the realEditor for the return value.
	 */
	public boolean shouldSelectCell(EventObject event) {
		return realEditor.shouldSelectCell(event);
	}

	/**
	 * If the realEditor will allow editing to stop, the realEditor is
	 * removed and true is returned, otherwise false is returned.
	 */
	public boolean stopCellEditing() {
		if (realEditor.stopCellEditing()) {
			if (editingComponent != null)
				editingContainer.remove(editingComponent);
			editingComponent = null;
			return true;
		}
		return false;
	}

	/**
	 * Messages cancelCellEditing to the realEditor and removes it from this
	 * instance.
	 */
	public void cancelCellEditing() {
		realEditor.cancelCellEditing();
		if (editingComponent != null)
			editingContainer.remove(editingComponent);
		editingComponent = null;
	}

	/**
	 * Adds the CellEditorListener.
	 */
	public void addCellEditorListener(CellEditorListener l) {
		realEditor.addCellEditorListener(l);
	}

	/**
	  * Removes the previously added CellEditorListener l.
	  */
	public void removeCellEditorListener(CellEditorListener l) {
		realEditor.removeCellEditorListener(l);
	}

	//
	// TreeSelectionListener
	//

	/**
	 * Resets lastPath.
	 */
	public void valueChanged(GraphSelectionEvent e) {
		if (graph != null) {
			if (graph.getSelectionCount() == 1)
				lastCell = graph.getSelectionCell();
			else
				lastCell = null;
		}
	}

	//
	// ActionListener (for Timer).
	//

	/**
	 * Messaged when the timer fires, this will start the editing
	 * session.
	 */
	public void actionPerformed(ActionEvent e) {
		if (graph != null)
			graph.startEditingAtCell(lastCell);
	}

	//
	// Local methods
	//

	/**
	 * Sets the tree currently editing for. This is needed to add
	 * a selection listener.
	 */
	protected void setGraph(JGraph newGraph) {
		if (graph != newGraph) {
			if (graph != null)
				graph.removeGraphSelectionListener(this);
			graph = newGraph;
			if (graph != null)
				graph.addGraphSelectionListener(this);
		}
	}

	/**
	 * Returns true if <code>event</code> is a MouseEvent and the click
	 * count is 1.
	 */
	protected boolean shouldStartEditingTimer(EventObject event) {
		if ((event instanceof MouseEvent)
			&& SwingUtilities.isLeftMouseButton((MouseEvent) event)) {
			MouseEvent me = (MouseEvent) event;

			return (
				me.getClickCount() == 1 && inHitRegion(me.getX(), me.getY()));
		}
		return false;
	}

	/**
	 * Returns true if <code>event</code> is null, or it is a MouseEvent
	 * with a click count > 2 and inHitRegion returns true.
	 */
	protected boolean canEditImmediately(EventObject event) {
		if ((event instanceof MouseEvent)
			&& SwingUtilities.isLeftMouseButton((MouseEvent) event)) {
			MouseEvent me = (MouseEvent) event;

			return inHitRegion(me.getX(), me.getY());
		}
		return (event == null);
	}

	/**
	 * Should return true if the passed in location is a valid mouse location
	 * to start editing from. This is implemented to return false if
	 * <code>x</code> is <= the width of the icon and icon gap displayed
	 * by the renderer. In other words this returns true if the user
	 * clicks over the text part displayed by the renderer, and false
	 * otherwise.
	 */
	protected boolean inHitRegion(double x, double y) {
		// This is only ever called when within the cell bounds,
		// so this is ok for the default case.
		return true;
	}

	protected void determineOffset(
		JGraph graph,
		Object value,
		boolean isSelected) {
		editingIcon = null;
		offsetX = graph.getHandleSize();
		offsetY = graph.getHandleSize();
	}

	/**
	 * Invoked just before editing is to start. Will add the
	 * <code>editingComponent</code> to the
	 * <code>editingContainer</code>.
	 */
	protected void prepareForEditing() {
		editingContainer.add(editingComponent);
	}

	/**
	 * Creates the container to manage placement of editingComponent.
	 */
	protected Container createContainer() {
		return new EditorContainer();
	}

	/**
	 * This is invoked if a TreeCellEditor is not supplied in the constructor.
	 * It returns a TextField editor.
	 */
	protected GraphCellEditor createGraphCellEditor() {
		Border aBorder = UIManager.getBorder("Tree.editorBorder");
		DefaultRealEditor editor =
			new DefaultRealEditor(new DefaultTextField(aBorder)) {
			public boolean shouldSelectCell(EventObject event) {
				boolean retValue = super.shouldSelectCell(event);
				getComponent().requestFocus();
				return retValue;
			}
		};

		// One click to edit.
		editor.setClickCountToStart(1);
		return editor;
	}

	// Serialization support.
	private void writeObject(ObjectOutputStream s) throws IOException {
		Vector values = new Vector();

		s.defaultWriteObject();
		// Save the realEditor, if its Serializable.
		if (realEditor instanceof Serializable) {
			values.addElement("realEditor");
			values.addElement(realEditor);
		}
		s.writeObject(values);
	}

	private void readObject(ObjectInputStream s)
		throws IOException, ClassNotFoundException {
		s.defaultReadObject();

		Vector values = (Vector) s.readObject();
		int indexCounter = 0;
		int maxCounter = values.size();

		if (indexCounter < maxCounter
			&& values.elementAt(indexCounter).equals("realEditor")) {
			realEditor = (GraphCellEditor) values.elementAt(++indexCounter);
			indexCounter++;
		}
	}

	/**
	 * TextField used when no editor is supplied. This textfield locks into
	 * the border it is constructed with. It also prefers its parents
	 * font over its font. And if the renderer is not null and no font
	 * has been specified the preferred height is that of the renderer.
	 */
	public class DefaultTextField extends JTextField {
		/** Border to use. */
		protected Border border;

		/**
		 * Constructs a DefaultTreeCellEditor$DefaultTextField object.
		 *
		 * @param border  a Border object
		 */
		public DefaultTextField(Border border) {
			this.border = border;
		}

		/**
		 * Overrides <code>JComponent.getBorder</code> to
		 * returns the current border.
		 */
		public Border getBorder() {
			return border;
		}

		// implements java.awt.MenuContainer
		public Font getFont() {
			Font font = super.getFont();

			// Prefer the parent containers font if our font is a
			// FontUIResource
			if (font instanceof FontUIResource) {
				Container parent = getParent();

				if (parent != null && parent.getFont() != null)
					font = parent.getFont();
			}
			return font;
		}
	}

	/**
	 * Container responsible for placing the editingComponent.
	 */
	public class EditorContainer extends Container {
		/**
		 * Constructs an EditorContainer object.
		 */
		public EditorContainer() {
			setLayout(null);
		}

		/**
		 * Overrides <code>Container.paint</code> to paint the node's
		 * icon and use the selection color for the background.
		 */
		public void paint(Graphics g) {
			Dimension size = getSize();

			// Then the icon.
			if (editingIcon != null) {
				int yLoc = 0;
				int xLoc = 0;
				editingIcon.paintIcon(this, g, xLoc, yLoc);
			}

			// Border selection color
			Color background = getBorderSelectionColor();
			if (background != null) {
				g.setColor(background);
				g.drawRect(0, 0, size.width - 1, size.height - 1);
			}
			super.paint(g);
		}

		/**
		 * Lays out this Container.  If editing, the editor will be placed at
		 * offset in the x direction and 0 for y.
		 */
		public void doLayout() {
			if (editingComponent != null) {
				Dimension cSize = getSize();
				int h = (int) editingComponent.getPreferredSize().getHeight();
				int minw = 45;
				int w = (int) editingComponent.getPreferredSize().getWidth()+5;
				int maxw = (int) editingComponent.getMaximumSize().getWidth();
				if (editingContainer.getParent() != null
						&& maxw > editingContainer.getParent().getWidth())
					w = cSize.width - offsetX;
				else
					w = Math.max(minw, Math.min(w, maxw));
				editingComponent.setBounds(
					offsetX,
					offsetY,
					w,
					h);
			}
		}

		/**
		 * Returns the preferred size for the Container.  This will be
		 * the preferred size of the editor offset by offset.
		 */
		public Dimension getPreferredSize() {
			if (editingComponent != null) {
				Dimension pSize = editingComponent.getPreferredSize();

				pSize.width += offsetX + 2;
				pSize.height += offsetY + 2;

				// Make sure width is at least 50.
				// and height at least 20
				int iwidth = 50;
				if (editingIcon != null) {
					iwidth = Math.max(editingIcon.getIconWidth(), iwidth);
				}
				pSize.height = Math.max(pSize.height, 24); // Offset 4
				pSize.width = Math.max(pSize.width+5, iwidth);
				return pSize;
			}
			return new Dimension(0, 0);
		}
	}
}
