package org.apromore.jgraph.components.labels;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.EventObject;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.DefaultGraphCellEditor;
import org.apromore.jgraph.graph.GraphCellEditor;
import org.apromore.jgraph.graph.GraphConstants;


/**
 * In-place editor for rich text multiline values.
 */
public class RichTextEditor extends DefaultGraphCellEditor {

	/**
	 * Constructs a new rich text editor.
	 */
	public RichTextEditor() {
		super();
	}

	/**
	 * Utlitiy editor for rich text values.
	 */
	class RealCellEditor extends AbstractCellEditor implements
			GraphCellEditor {

		/**
		 * Holds the component used for editing.
		 */
		JTextPane editorComponent = new JTextPane();

		/**
		 * Constructs a new editor that supports shift- and control-enter
		 * keystrokes to insert newlines into the editing component.
		 */
		public RealCellEditor() {
			editorComponent.setBorder(UIManager
					.getBorder("Tree.editorBorder"));
			// editorComponent.setLineWrap(true);
			// editorComponent.setWrapStyleWord(true);
			// substitute a JTextArea's VK_ENTER action with our own that
			// will stop an edit.
			editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
			editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
							KeyEvent.SHIFT_DOWN_MASK), "metaEnter");
			editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
							KeyEvent.CTRL_DOWN_MASK), "metaEnter");
			editorComponent.getActionMap().put("enter",
					new AbstractAction() {

						public void actionPerformed(ActionEvent e) {
							stopCellEditing();
						}
					});
			editorComponent.getActionMap().put("metaEnter",
					new AbstractAction() {

						public void actionPerformed(ActionEvent e) {
							Document doc = editorComponent.getDocument();
							try {
								doc.insertString(editorComponent
										.getCaretPosition(), "\n", null);
							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}
						}
					});
		}

		/**
		 * Sets of the editor for editing the rich text value.
		 */
		public Component getGraphCellEditorComponent(JGraph graph,
				Object value, boolean isSelected) {
			Rectangle2D cellBounds = graph.getCellBounds(value);
			if (cellBounds != null) {
				Dimension maxSize = new Dimension((int) cellBounds
						.getWidth(), (int) cellBounds.getHeight());
				editorComponent.setMaximumSize(maxSize);
			}
			Object cell = value;
			value = graph.getModel().getValue(value);
			if (value instanceof RichTextBusinessObject
					&& ((RichTextBusinessObject) value).isRichText())
				try {
					StyledDocument document = (StyledDocument) editorComponent
							.getDocument();

					((RichTextValue) ((RichTextBusinessObject) value)
							.getValue()).insertInto(document);

					// Workaround for trailing newline
					if (document.getLength() > 0)
						document.remove(document.getLength() - 1, 1);

					// Workaround for the alignment not being serializable:
					// We use the label's alignment as the global alignment
					CellView view = graph.getGraphLayoutCache().getMapping(
							cell, false);
					if (view != null) {
						Map map = view.getAllAttributes();
						int align = GraphConstants
								.getHorizontalAlignment(map);
						SimpleAttributeSet sas = new SimpleAttributeSet();
						align = (align == JLabel.CENTER) ? StyleConstants.ALIGN_CENTER
								: (align == JLabel.RIGHT) ? StyleConstants.ALIGN_RIGHT
										: StyleConstants.ALIGN_LEFT;
						StyleConstants.setAlignment(sas, align);
						document.setParagraphAttributes(0, document
								.getLength(), sas, true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			else {
				editorComponent.setText(value.toString());
			}
			editorComponent.selectAll();
			editorComponent.getDocument().addDocumentListener(
					new DocumentListener() {

						public void updateSize() {
							SwingUtilities.invokeLater(new Runnable() {

								public void run() {
									Container container = editorComponent
											.getParent();
									if (container != null) {
										container.doLayout();
										container.setSize(editingContainer
												.getPreferredSize());
										container.invalidate();
									}
								}
							});
						}

						public void insertUpdate(DocumentEvent arg0) {
							updateSize();
						}

						public void removeUpdate(DocumentEvent arg0) {
							updateSize();
						}

						public void changedUpdate(DocumentEvent arg0) {
							updateSize();
						}
					});
			return editorComponent;
		}

		/**
		 * Returns the rich text value to be stored in the user object.
		 */
		public Object getCellEditorValue() {
			return new RichTextValue(editorComponent.getDocument());
		}

		/**
		 * Transfers the focus to the editing component.
		 */
		public boolean shouldSelectCell(EventObject event) {
			editorComponent.requestFocus();
			return super.shouldSelectCell(event);
		}
	}

	/**
	 * Overriding this in order to set the size of an editor to that of an
	 * edited view.
	 */
	public Component getGraphCellEditorComponent(JGraph graph, Object cell,
			boolean isSelected) {
		Component component = super.getGraphCellEditorComponent(graph,
				cell, isSelected);
		// set the size of an editor to that of the view
		CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
		Rectangle2D tmp = view.getBounds();
		editingComponent.setBounds((int) tmp.getX(), (int) tmp.getY(),
				(int) tmp.getWidth(), (int) tmp.getHeight());
		// I have to set a font here instead of in the
		// RealCellEditor.getGraphCellEditorComponent() because
		// I don't know what cell is being edited when in the
		// RealCellEditor.getGraphCellEditorComponent().
		Font font = GraphConstants.getFont(view.getAllAttributes());
		editingComponent.setFont((font != null) ? font : graph.getFont());
		return component;
	}

	/**
	 * Returns a new RealCellEditor.
	 */
	protected GraphCellEditor createGraphCellEditor() {
		return new RichTextEditor.RealCellEditor();
	}

	/**
	 * Overriting this so that I could modify an editor container. See also
	 * <A
	 * HREF="http://sourceforge.net/forum/forum.php?thread_id=781479&forum_id=140880">here</A>.
	 */
	protected Container createContainer() {
		return new RichTextEditor.ModifiedEditorContainer();
	}

	/**
	 * Utitlitiy container with a custom layout.
	 */
	class ModifiedEditorContainer extends EditorContainer {

		/**
		 * Performs the layout of the components in the container.
		 */
		public void doLayout() {
			super.doLayout();
			// substract 2 pixels that were added to the preferred size of
			// the container for the border.
			Dimension cSize = getSize();
			Dimension dim = editingComponent.getSize();
			editingComponent.setSize(dim.width - 2, dim.height);
			// reset container's size based on a potentially new preferred
			// size of a real editor.
			setSize(cSize.width, getPreferredSize().height);
		}
	}
}

