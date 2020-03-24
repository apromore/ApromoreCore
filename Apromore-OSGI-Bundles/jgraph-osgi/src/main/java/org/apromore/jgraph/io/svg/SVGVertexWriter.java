package org.apromore.jgraph.io.svg;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.GraphConstants;
import org.apromore.jgraph.graph.GraphLayoutCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGVertexWriter {

	/** Represents the shadow color hex encoding. */
	public static String HEXCOLOR_SHADOW = SVGUtils
			.getHexEncoding(Color.DARK_GRAY);

	/** Represents the shadow opacity. */
	public static double SHADOW_OPACITY = 0.5;

	/** Represents the shadow distance. */
	public static int SHADOW_DISTANCE = 3;

	/**
	 * Returns the SVG representation of the specified node.
	 * 
	 * @param writer
	 * @param document
	 * @param cache
	 * @param view
	 * @param dx
	 * @param dy
	 * @return an XML node describing the specified node
	 */
	public Node createNode(SVGGraphWriter writer, Document document,
			GraphLayoutCache cache, CellView view, double dx, double dy) {
		Rectangle2D bounds = view.getBounds();
		Map attributes = view.getAllAttributes();
		Element href = (Element) document.createElement("a");
		String link = GraphConstants.getLink(attributes);
		if (link != null) {
			href.setAttribute("xlink:href", link);
		}

		// Specifies the shape geometry
		int shapeType = SVGGraphConstants.getShape(attributes);
		Color background = GraphConstants.getBackground(attributes);
		String hexBackground = null;
		if (background != null) {
			hexBackground = SVGUtils.getHexEncoding(background);
		}
		Color gradient = GraphConstants.getGradientColor(attributes);
		String hexGradient = null;
		if (gradient != null) {
			// TODO need proper definition for gradient colours
			// For now put the gradient colour in the background
			// In future need a proper definition for SVG
			hexBackground = SVGUtils.getHexEncoding(gradient);
		}
		Color borderColor = GraphConstants.getBorderColor(attributes);
		String hexLineColor = null;
		if (borderColor != null) {
			hexLineColor = SVGUtils.getHexEncoding(borderColor);
		}
		float lineWidth = GraphConstants.getLineWidth(attributes);

		// Adds a drop shadow
		boolean dropShadow = SVGGraphConstants.isShadow(attributes);
		if (dropShadow) {
			int dist = SHADOW_DISTANCE;
			href.appendChild(writer.createShapeNode(document, shapeType,
					bounds, dx - dist, dy - dist, HEXCOLOR_SHADOW, null,
					"none", lineWidth, SHADOW_OPACITY, false));
		}
		href.appendChild(writer.createShapeNode(document, shapeType, bounds,
				dx, dy, hexBackground, hexGradient, hexLineColor, lineWidth,
				1.0, false));

		// Adds the image
		// This is currently not implemented due to bugs in all
		// known SVG viewers, either ignoring the image at all
		// or ignoring the individual sizes for the image (firefox)
		// String imageURL = "http://www.jgraph.co.uk/images/logo.gif";
		// if (imageURL != null) {
		// Element image = (Element) document.createElement("image");
		// image.setAttribute("x", String.valueOf(bounds.getX() - dx));
		// image.setAttribute("y", String.valueOf(bounds.getY() - dy));
		// image.setAttribute("w", String.valueOf(bounds.getWidth()));
		// image.setAttribute("xlink:href", imageURL);
		// image.setAttribute("h", String.valueOf(bounds.getHeight()));
		// image.setAttribute("y", String.valueOf(bounds.getY() - dy));
		// image.setAttribute("preserveAspectRatio", "none");
		// href.appendChild(image);
		// }

		// Draws the labels stored in the user object
		Object[] values = writer.getLabels(view);
		int x = (int) (bounds.getX() - dx + bounds.getWidth() / 2);
		Font font = GraphConstants.getFont(attributes);
		int fontsize = (font != null) ? font.getSize() : 11;
		int textHeight = (fontsize + SVGUtils.LINESPACING) * values.length;
		int yOffset = (int) ((bounds.getHeight() - textHeight) / 2) + fontsize;
		for (int i = 0; i < values.length; i++) {
			Color fontColor = GraphConstants.getForeground(attributes);
			String hexFontColor = null;
			if (fontColor != null) {
				hexFontColor = SVGUtils.getHexEncoding(fontColor);
			}
			if (values[i] instanceof Node) {
				// Import the node since it comes from a different document
				Node importedNode = document.importNode((Node)values[i], true);
				// Create an empty text node
				Node textNode = writer
				.createTextNode(document, "", "middle", font,
						hexFontColor, x,
						(int) (bounds.getY() + yOffset - dy));
				href.appendChild(textNode);
				textNode.appendChild(importedNode);
			} else {
				String label = String.valueOf(values[i]);
				href.appendChild(writer
						.createTextNode(document, label, "middle", font,
								hexFontColor, x,
								(int) (bounds.getY() + yOffset - dy)));
			}
			yOffset += fontsize + SVGUtils.LINESPACING;
		}

		return href;
	}

}
