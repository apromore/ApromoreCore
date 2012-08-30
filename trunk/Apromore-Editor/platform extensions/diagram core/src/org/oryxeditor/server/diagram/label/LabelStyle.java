package org.oryxeditor.server.diagram.label;

import java.awt.Color;

/**
 * Contains information about font and background color of a label
 * @author philipp.maschke
 *
 */
public class LabelStyle {
	
	private String fontFamily; //TODO make as enum?
	private Double fontSize;
	private boolean bold;
	private boolean italic;
	private Color fill;
	
	
	public String getFontFamily() {
		return fontFamily;
	}
	
	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}
	
	public Double getFontSize() {
		return fontSize;
	}
	
	public void setFontSize(Double fontSize) {
		this.fontSize = fontSize;
	}
	
	public Color getFill() {
		return fill;
	}
	
	public void setFill(Color fill) {
		this.fill = fill;
	}

	
	public boolean isBold() {
		return bold;
	}
	public void setBold(boolean bold) {
		this.bold = bold;
	}

	
	public boolean isItalic() {
		return italic;
	}
	public void setItalic(boolean italic) {
		this.italic = italic;
	}
	
}
