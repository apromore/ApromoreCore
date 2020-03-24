package org.apromore.jgraph.components.labels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.KeyStroke;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.graph.DefaultGraphCellEditor;
import org.apromore.jgraph.graph.GraphCellEditor;

/**
 * In-place editor for rich text multiline values.
 */
public class RedirectingEditor extends DefaultGraphCellEditor {

	/**
	 * Constructs a new rich text editor.
	 */
	public RedirectingEditor() {
		super();
	}

	/**
	 * Utlitiy editor for rich text values.
	 */
	class RealCellEditor extends AbstractCellEditor implements
			GraphCellEditor {

		/**
		 * Holds the component value if one exists.
		 */
		Component componentValue = null;

		/**
		 * Sets of the editor for editing the rich text value.
		 */
		public Component getGraphCellEditorComponent(JGraph graph,
				Object value, boolean isSelected) {
			Rectangle2D cellBounds = graph.getCellBounds(value);
			value = graph.getModel().getValue(value);
			if (value instanceof RichTextBusinessObject
					&& ((RichTextBusinessObject) value).isComponent()) {
				
				// Clones the heavyweight to avoid repaint problems on Windows
				value = ((RichTextBusinessObject) value).clone();
				
				Dimension maxSize = new Dimension((int) cellBounds
						.getWidth() - 6, (int) cellBounds.getHeight());
				componentValue = (Component) ((RichTextBusinessObject) value)
						.getValue();
				componentValue.invalidate();
				if (componentValue instanceof JComponent) {
					JComponent c = (JComponent) componentValue;
					c.setMaximumSize(maxSize);
					c.setPreferredSize(maxSize);
					c.setMinimumSize(maxSize);
				}
				if (componentValue instanceof JTree) {
					JTree tree = (JTree) componentValue;
					tree.setSelectionInterval(0, 0);
				}

				// Stops cell editing when enter is pressed
				if (componentValue instanceof JComponent) {
					JComponent comp = (JComponent) componentValue;
					comp.getInputMap(JComponent.WHEN_FOCUSED).put(
							KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
							"enter");
					comp.getInputMap(
							JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
							.put(
									KeyStroke.getKeyStroke(
											KeyEvent.VK_ENTER, 0), "enter");
					comp.getActionMap().put("enter", new AbstractAction() {

						public void actionPerformed(ActionEvent e) {
							stopCellEditing();
						}
					});
				}
				return componentValue;
			}
			return null;
		}

		/**
		 * Returns the rich text value to be stored in the user object.
		 */
		public Object getCellEditorValue() {
			return componentValue;
		}

		/**
		 * If the realEditor will allow editing to stop, the realEditor is
		 * removed and true is returned, otherwise false is returned.
		 */
		public boolean stopCellEditing() {
			if (componentValue instanceof JTree)
				((JTree) componentValue).clearSelection();
			return super.stopCellEditing();
		}

		/**
		 * Messages cancelCellEditing to the realEditor and removes it from
		 * this instance.
		 */
		public void cancelCellEditing() {
			if (componentValue instanceof JTree)
				((JTree) componentValue).clearSelection();
			super.cancelCellEditing();
		}

		/**
		 * Transfers the focus to the editing component.
		 */
		public boolean shouldSelectCell(EventObject event) {
			componentValue.requestFocus();
			return super.shouldSelectCell(event);
		}
	}

	/**
	 * Returns a new RealCellEditor.
	 */
	protected GraphCellEditor createGraphCellEditor() {
		return new RedirectingEditor.RealCellEditor();
	}

}
