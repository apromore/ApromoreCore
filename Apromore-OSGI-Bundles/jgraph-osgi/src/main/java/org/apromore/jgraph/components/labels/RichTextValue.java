/* 
 * $Id: RichTextValue.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved.
 * 
 * See LICENSE file for license details. If you are unable to locate
 * this file please contact info (at) jgraph (dot) com.
 */
package org.apromore.jgraph.components.labels;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * Rich text replacement for string values in {@link RichTextBusinessObject}.
 * This requirs the {@link MultiLineVertexView} to provide a rich text editor
 * and renderer.
 * 
 * @see RichTextGraphModel
 * @see VertexRenderer
 * @see RTFEditorKit
 */
public class RichTextValue implements Serializable, Cloneable {

	/**
	 * Holds the shared editor kit for creating new documents.
	 */
	public static RTFEditorKit editorKit = new RTFEditorKit();

	/**
	 * Holds the rich text as an RTF encoded text.
	 */
	protected String richText;

	/**
	 * A plain-text representation of the rich text is always keps along with
	 * the rich text value to speedup the {@link #toString()} method.
	 */
	protected String plainText;

	/**
	 * Constructs a new empty rich text value.
	 */
	public RichTextValue() {
		// empty
	}

	/**
	 * Constructs a new rich text value using the specified document.
	 * 
	 * @param document
	 *            The document to obtain the rich text from.
	 */
	public RichTextValue(Document document) {
		setRichText(getRichText(document));
	}

	/**
	 * Constructs a new rich text document using the string text.
	 * 
	 * @param stringValue
	 *            The string to use as the initial value.
	 */
	public RichTextValue(String stringValue) {
		this(createDefaultDocument(stringValue));
	}

	/**
	 * Inserts this rich text into the specified component. This implementation
	 * silently ignores all exceptions.
	 * 
	 * @param document
	 *            The document to insert the rich text into.
	 */
	public void insertInto(Document document) {
		ByteArrayInputStream bin = new ByteArrayInputStream(richText.getBytes());
		try {
			document.remove(0, document.getLength());
			editorKit.read(bin, document, 0);
			bin.close();
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Returns the richt text value as an RTF encoded string.
	 * 
	 * @return Returns the rich text.
	 */
	public String getRichText() {
		return richText;
	}

	/**
	 * Sets the richt text value as an RTF encoded string and updates
	 * {@link #plainText}.
	 * 
	 * @param richText
	 *            The rich text to set.
	 */
	public void setRichText(String richText) {
		this.richText = richText;
		// Updates the plain text version of the rich text.
		plainText = getPlainText(this);
	}

	/**
	 * Returns the plain text representation of this rich text value.
	 * 
	 * @return Returns {@link #plainText}.
	 */
	public String toString() {
		return plainText;
	}

	/**
	 * Returns the rich text encoded RTF string from the specified document.
	 * 
	 * @param document
	 *            The document to be converted.
	 * @return Returns the RTF encoded document.
	 */
	public static String getRichText(Document document) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			editorKit.write(bos, document, 0, document.getLength());
			bos.flush();
		} catch (Exception e) {
			// ignore
		}
		return new String(bos.toByteArray());
	}

	/**
	 * Returns a plain text representation of the specified rich text value. If
	 * an exception occurs during conversion then the RTF encoded string is
	 * returned instead.
	 * 
	 * @param richText
	 *            The rich text value to be converted.
	 * @return Returns the plain text representation.
	 */
	public static String getPlainText(RichTextValue richText) {
		Document doc = createDefaultDocument();
		richText.insertInto(doc);
		try {
			return doc.getText(0, doc.getLength()).trim();
		} catch (BadLocationException e) {
			// ignore
		}
		return richText.getRichText();
	}

	/**
	 * Hook for subclassers to create a default document. This implementation
	 * uses {@link #createDefaultDocument(String)} with a value of null.
	 * 
	 * @return Returns a new empty default document.
	 */
	protected static Document createDefaultDocument() {
		return createDefaultDocument(null);
	}

	/**
	 * Hook for subclassers to create a default document. This implementation
	 * uses {@link #editorKit} to create the document and sets its value.
	 * 
	 * @return Returns a new empty default document.
	 */
	public static Document createDefaultDocument(String value) {
		Document document = editorKit.createDefaultDocument();
		if (value != null) {
			try {
				// FIXME: Use font for "" (GraphConstants.DEFAULTFONT)
				document.insertString(0, value, null);
			} catch (BadLocationException e1) {
				// ignore
			}
		}
		return document;
	}

}
