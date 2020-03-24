package org.apromore.jgraph.io.svg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Map;

import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.EdgeView;
import org.apromore.jgraph.graph.GraphConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGEdgeWriter {

	/** Represents the default line color. */
	public static String DEFAULT_LINE_COLOR = SVGUtils.HEXCOLOR_BLACK;

	public Node createNode(SVGGraphWriter writer, Document document,
			CellView view, double dx, double dy) {
		Map attributes = view.getAllAttributes();

		Element href = (Element) document.createElement("a");
		String link = GraphConstants.getLink(attributes);
		if (link != null) {
			href.setAttribute("xlink:href", link);
		}

		if (view instanceof EdgeView) {
			
			// ******** Start Path element **************
			Element path = (Element) document.createElement("path");
			EdgeView edge = (EdgeView) view;

			path.setAttribute("fill", "none");
			Color lineColor = GraphConstants.getLineColor(attributes);
			String hexLineColor = null;
			if (lineColor != null) {
				hexLineColor = SVGUtils.getHexEncoding(lineColor);
			} else {
				hexLineColor = DEFAULT_LINE_COLOR;
			}
			path.setAttribute("stroke", hexLineColor);
			float lineWidth = GraphConstants.getLineWidth(attributes);
			path.setAttribute("stroke-width", String.valueOf(lineWidth));
			// Dash pattern
			float[] dash = GraphConstants.getDashPattern(attributes);
			if (dash != null) {
				// Convert float array to string
				String dashValue = "";
				for (int i = 0; i < dash.length; i++) {
					Float wrapperFloat = new Float(dash[i]);
					dashValue += wrapperFloat.toString();
					if (i != dash.length-1) {
						dashValue += ", ";
					}
				}
				path.setAttribute("stroke-dasharray", dashValue);
			}

			// Computes the d attribute
			Point2D point = edge.getPoint(0);
			String d = "M " + (point.getX() - dx) + " " + (point.getY() - dy);
			for (int i = 1; i < edge.getPointCount(); i++) {
				point = edge.getPoint(i);
				d += " L " + (point.getX() - dx) + " " + (point.getY() - dy);
			}
			path.setAttribute("d", d);
			int lineBegin = GraphConstants.getLineBegin(attributes);
			int lineEnd = GraphConstants.getLineEnd(attributes);
			String styleAttributes = new String("");
			// TODO should draw arrows correctly instead of only having one type
			if (true) {
				styleAttributes += "marker-start: url(#endMarker);";
//				Element startMarker = (Element) document.createElement("marker-start");
//				startMarker.setAttribute("stroke", hexLineColor);
////				path.setAttribute("marker-start", "url(#endMarker)");
////				path.setAttribute("stroke", hexLineColor);
//				href.appendChild(startMarker);
				
			}
			// TODO should draw arrows correctly instead of only having one type
			if (true) {
				styleAttributes += "marker-end: url(#startMarker);";
//				path.setAttribute("marker-end", "url(#startMarker)");
//				path.setAttribute("stroke", hexLineColor);
			}
			styleAttributes += " stroke: " + hexLineColor + ";";
			path.setAttribute("style", styleAttributes);

			// Finds center point for labels
			Point center = null;
			int mid = edge.getPointCount() / 2;
			if (edge.isLoop()) {
				Point2D tmp = edge.getPoint(0);
				Point2D tmp2 = edge.getLabelVector();
				center = new Point((int) (tmp.getX() + tmp2.getX() - dx),
						(int) (tmp.getY() + tmp2.getY() - dy));
			} else if (edge.getPointCount() % 2 == 1) {
				Point2D tmp = edge.getPoint(mid);
				center = new Point((int) (tmp.getX() - dx),
						(int) (tmp.getY() - dy));
			} else {
				Point2D p1 = edge.getPoint(mid - 1);
				Point2D p2 = edge.getPoint(mid);
				center = new Point((int) (p1.getX() + (p2.getX() - p1.getX())
						/ 2 - dx), (int) (p1.getY() + (p2.getY() - p1.getY())
						/ 2 - dy));
			}
			href.appendChild(path);
			// ******** End Path element **************
			
			
			// Draws the labels in the center of the line
			Object[] values = writer.getLabels(edge);
			int yOffset = 0;
			for (int i = 0; i < values.length; i++) {
				String label = String.valueOf(values[i]);
				Font font = GraphConstants.getFont(attributes);
				Color fontColor = GraphConstants.getForeground(attributes);
				String hexFontColor = null;
				if (fontColor != null) {
					hexFontColor = SVGUtils.getHexEncoding(fontColor);
				}
				int y = (int) (center.y + yOffset);
				href.appendChild(writer.createTextNode(document, label,
						"middle", font, hexFontColor, center.x, y));
				yOffset += ((font != null) ? font.getSize() : 11)
						+ SVGUtils.LINESPACING;
			}
		}

		return href;
	}

}
