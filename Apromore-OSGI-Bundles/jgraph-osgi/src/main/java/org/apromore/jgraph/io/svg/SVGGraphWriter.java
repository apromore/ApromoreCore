package org.apromore.jgraph.io.svg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.GraphLayoutCache;
import org.apromore.jgraph.graph.GraphModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGGraphWriter {

	/**
	 * Specifies the font used for the title of the diagram.
	 */
	public static Font TITLE_FONT = new Font("Dialog", 0, 16);

	/**
	 * Specifies the spacing between the title and the diagram.
	 */
	public static int TITLE_VSPACING = 10;

	/**
	 * The assumes proportion between the font height and the average character
	 * width.
	 */
	public static double FONT_PROPORTION_FACTOR = 1.5;

	/**
	 * Specifies the color the diagram title.
	 */
	public static String TITLE_HEXCOLOR = SVGUtils
			.getHexEncoding(Color.DARK_GRAY);

	/**
	 * Holds object that creates the SVG code for vertices.
	 */
	protected SVGVertexWriter vertexFactory = new SVGVertexWriter();

	/**
	 * Holds the object that creates the SVG code for edges.
	 */
	protected SVGEdgeWriter edgeFactory = new SVGEdgeWriter();

	/**
	 * Holds the gradients created dynamically during the rendering process and
	 * added later to the defs part of the SVG output.
	 */
	protected Map gradients = new Hashtable();

	/**
	 * Holds the bounds of the graph.
	 */
	protected Rectangle viewBox = new Rectangle(0, 0, 0, 0);

	/**
	 * Writes the SVG.
	 */
	public void write(OutputStream out, String title, GraphLayoutCache cache,
			int inset) {
		try {
			Document document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			document.appendChild(createNode(document, title, cache, inset));
			TransformerFactory.newInstance().newTransformer().transform(
					new DOMSource(document), new StreamResult(out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Node createNode(Document document, String title,
			GraphLayoutCache cache, int inset)
			throws ParserConfigurationException {

		// Collects basic geometric details
		Rectangle2D bounds = GraphLayoutCache.getBounds(cache.getAllViews());
		double dx = bounds.getX() - inset;
		double dy = bounds.getY() - inset;
		int titleheight = 0;
		int titlewidth = 0;

		// Adds the title to the diagram geometry
		Node titleNode = null;
		if (title != null && title.length() > 0) {
			Font titleFont = TITLE_FONT;
			titleheight = titleFont.getSize() + TITLE_VSPACING;
			titlewidth = (int) (title.length() * titleFont.getSize() / FONT_PROPORTION_FACTOR);
			titleNode = createTextNode(document, title, null, titleFont,
					TITLE_HEXCOLOR, inset, inset + titleheight - TITLE_VSPACING);
		}
		double width = Math.max(titlewidth, bounds.getWidth() + 2 * inset);
		double height = bounds.getHeight() + 2 * inset + titleheight;
		Node root = createRoot(document, width, height, inset);

		// Adds defs for filters, gradients, markers etc
		Element defs = (Element) document.createElement("defs");
		// Define arrow markers
		Element g = (Element) document.createElement("g");
		g.setAttribute("id", "arrowMarker");
		defs.appendChild(g);
		Element stroke = (Element) document.createElement("g");
		stroke.setAttribute("stroke-width", "0");
		g.appendChild(stroke);
		Element path = (Element) document.createElement("path");
		path.setAttribute("d", "M 4 -2 L 0 0 L 4 2 L 3 1 L 3 -1 L 4 -2");
		stroke.appendChild(path);
		Element marker = (Element) document.createElement("marker");
		marker.setAttribute("id", "startMarker");
		marker.setAttribute("markerWidth", "48");
		marker.setAttribute("markerHeight", "24");
		marker.setAttribute("viewBox", "-4 -4 25 5");
		marker.setAttribute("orient", "auto");
		marker.setAttribute("refX", "0");
		marker.setAttribute("refY", "0");
		marker.setAttribute("markerUnits", "strokeWidth");
		defs.appendChild(marker);
		g = (Element) document.createElement("g");
		marker.appendChild(g);
		Element use = (Element) document.createElement("use");
		use.setAttribute("xlink:href", "#arrowMarker");
		use.setAttribute("transform", "rotate(180)");
		use.setAttribute("stroke-width", "1");
		g.appendChild(use);

		// Also definition for non-rotated arrow
		marker = (Element) document.createElement("marker");
		marker.setAttribute("id", "endMarker");
		marker.setAttribute("markerWidth", "48");
		marker.setAttribute("markerHeight", "24");
		marker.setAttribute("viewBox", "-4 -4 25 5");
		marker.setAttribute("orient", "auto");
		marker.setAttribute("refX", "0");
		marker.setAttribute("refY", "0");
		marker.setAttribute("markerUnits", "strokeWidth");
		defs.appendChild(marker);
		g = (Element) document.createElement("g");
		marker.appendChild(g);
		use = (Element) document.createElement("use");
		use.setAttribute("xlink:href", "#arrowMarker");
		use.setAttribute("stroke-width", "1");
		g.appendChild(use);

		// This does currently not work in the SVG plugin
		// defs.appendChild(createDropShadowFilter(document, 2, 2, 2));
		root.appendChild(defs);

		// Adds the graph title
		if (titleNode != null) {
			root.appendChild(titleNode);
			dy -= titleheight;
		}

		// "Draws" all views, topmost first
		GraphModel model = cache.getModel();
		CellView[] views = cache.getAllViews();
		for (int i = 0; i < views.length; i++) {
			Object cell = views[i].getCell();
			if (!model.isPort(cell)) {

				// Invokes edge- or vertex renderer based on the cell type
				Node node = (model.isEdge(cell)) ? edgeFactory.createNode(this,
						document, views[i], dx, dy) : vertexFactory.createNode(
						this, document, cache, views[i], dx, dy);
				if (node != null) {
					root.appendChild(node);
				}
			}
		}

		// Collect the dynamically created gradient definitions
		// and add them to the definitions in the defs section
		Iterator it = gradients.values().iterator();
		while (it.hasNext()) {
			Object gradient = it.next();
			if (gradient instanceof Node) {
				defs.appendChild((Node) gradient);
			}
		}

		return root;
	}

	/**
	 * Creates the root SVG node with the basic information.
	 * 
	 * @param document
	 * @param w
	 * @param h
	 * @param inset
	 * @return a SVG node describing the SVG diagram
	 */
	protected Node createRoot(Document document, double w, double h, int inset) {
		Element svg = (Element) document.createElement("svg");
		svg.setAttribute("width", String.valueOf(w));
		svg.setAttribute("height", String.valueOf(h));
		svg.setAttribute("viewBox", "0 0 " + w + " " + h);
		svg.setAttribute("allowZoomAndPan", "true");
		svg.setAttribute("version", "1.1");
		svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
		svg.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
		viewBox.setFrame(inset, inset, w, h);
		return svg;
	}

	/**
	 * Returns or creates a gradient object for the specified colors.
	 * 
	 * @param document
	 * @param startColor
	 * @param endColor
	 * @return a node detailing the gradient on a cell element
	 */
	public Node getGradient(Document document, String startColor,
			String endColor) {
		Node gradient = null;
		if (startColor != null || endColor != null) {
			// Constructs a unique name for the gradient
			String gradientKey = "gradient"
					+ ((startColor != null) ? startColor.substring(1) : "none")
					+ "_"
					+ ((endColor != null) ? endColor.substring(1) : "none");
			gradient = (Element) gradients.get(gradientKey);
			if (gradient == null) {
				gradient = createGradient(document, gradientKey, startColor,
						endColor);

				// Caches the gradient for later adding to the defs
				// section and reuse by other cells
				gradients.put(gradientKey, gradient);
			}
		}
		return gradient;
	}

	/**
	 * Creates a new gradient element to be used my multiple cells.
	 * 
	 * @param document
	 * @param id
	 * @param startColor
	 * @param endColor
	 * @return a node detailing the gradient on a cell element
	 */
	protected Node createGradient(Document document, String id,
			String startColor, String endColor) {
		Element gradient = (Element) document.createElement("linearGradient");
		gradient.setAttribute("id", id);

		// Adds the start gradient details
		Element stop1 = (Element) document.createElement("stop");
		stop1.setAttribute("offset", "5%");
		stop1.setAttribute("stop-color", startColor);
		stop1.setAttribute("stop-opacity", "1");
		gradient.appendChild(stop1);

		// Adds the end gradient details
		Element stop2 = (Element) document.createElement("stop");
		stop2.setAttribute("offset", "95%");
		stop2.setAttribute("stop-color", endColor);
		stop2.setAttribute("stop-opacity", "1");
		gradient.appendChild(stop2);
		return gradient;
	}

	/**
	 * Creates a rect or ellipse element based on the specified values.
	 * 
	 * @param document
	 * @param shapeType
	 * @param bounds
	 * @param dx
	 * @param dy
	 * @param hexBackground
	 * @param hexGradient
	 * @param hexLineColor
	 * @param lineWidth
	 * @param opacity
	 * @param dropShadow
	 * @return a node detailing the shape on a vertex element
	 */
	public Node createShapeNode(Document document, int shapeType,
			Rectangle2D bounds, double dx, double dy, String hexBackground,
			String hexGradient, String hexLineColor, float lineWidth,
			double opacity, boolean dropShadow) {
		boolean isEllipse = shapeType == SVGGraphConstants.SHAPE_ELLIPSE;
		Element shape = (Element) document
				.createElement((isEllipse) ? "ellipse" : "rect");
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		if (isEllipse) {
			shape
					.setAttribute("cx", String.valueOf(bounds.getX() + w / 2
							- dx));
			shape
					.setAttribute("cy", String.valueOf(bounds.getY() + h / 2
							- dy));
			shape.setAttribute("rx", String.valueOf(w / 2));
			shape.setAttribute("ry", String.valueOf(h / 2));
		} else {
			shape.setAttribute("x", String.valueOf(bounds.getX() - dx));
			shape.setAttribute("y", String.valueOf(bounds.getY() - dy));
			if (shapeType == SVGGraphConstants.SHAPE_ROUNDRECT) {
				shape.setAttribute("rx", "5");
				shape.setAttribute("ry", "5");
			}
			shape.setAttribute("width", String.valueOf(w));
			shape.setAttribute("height", String.valueOf(h));
		}

		// Draws the background or gradient background
		if (hexGradient != null) {
			Node gradient = getGradient(document, hexBackground, hexGradient);
			String gradientId = gradient.getAttributes().getNamedItem("id")
					.getNodeValue();
			shape.setAttribute("fill", "url(#" + gradientId + ")");
		} else if (hexBackground != null) {
			shape.setAttribute("fill", hexBackground);
		}
		shape.setAttribute("opacity", String.valueOf(opacity));

		// This approach does not work, we draw two shapes instead, see
		// createDropShadowFilter
		// Adds a drop shadow
		// if (dropShadow) {
		// shape.setAttribute("filter", "url(#dropShadow)");
		// }

		// Draws the border
		shape.setAttribute("stroke", hexLineColor);
		shape.setAttribute("stroke-width", String.valueOf(lineWidth));
		return shape;
	}

	/**
	 * Creates a new text element for the specified details.
	 * 
	 * @param document
	 * @param label
	 * @param align
	 * @param font
	 * @param hexFontColor
	 * @param middleX
	 * @param y
	 * @return a node detailing the label on a cell element
	 */
	public Node createTextNode(Document document, String label, String align,
			Font font, String hexFontColor, int middleX, int y) {
		Element text = (Element) document.createElement("text");
		text.appendChild(document.createTextNode(label));
		int size = 11;
		if (font != null) {
			text.setAttribute("font-family", font.getFamily());
			text.setAttribute("font-size", String.valueOf(font.getSize2D()));
			size = font.getSize();
		} else {
			text.setAttribute("font-family", "Dialog");
			text.setAttribute("font-size", "11");
		}
		text.setAttribute("fill", hexFontColor);
		double aw = size / FONT_PROPORTION_FACTOR;
		int estWidth = (int) (label.length() * aw / 2);
		if (align != null) {
			if (middleX - estWidth < viewBox.getX()) {
				text.setAttribute("text-anchor", "start");
				middleX = (int) viewBox.getX();
			} else if (middleX + estWidth > viewBox.getWidth() - 2) {
				text.setAttribute("text-anchor", "end");
				middleX = (int) viewBox.getWidth() - 2;
			} else {
				text.setAttribute("text-anchor", align);
			}
		}
		text.setAttribute("x", String.valueOf(middleX));
		text.setAttribute("y", String.valueOf(Math
				.max(viewBox.getY() + size, y)));
		return text;
	}

	/**
	 * This implements the official recommended way of a drop shadow. However,
	 * it does not work with the SVG Plugin. We use a second shape behind the
	 * main shape as a workaround. This element is currently not added to the
	 * SVG output. See createNodeShape.
	 */
	protected Node createDropShadowFilter(Document document, int fuzziness,
			int dx, int dy) {
		// Creates the drop shadow effect for vertices
		Element dropShadow = (Element) document.createElement("filter");
		dropShadow.setAttribute("id", "dropShadow");
		dropShadow.setAttribute("x", "0");
		dropShadow.setAttribute("y", "0");
		dropShadow.setAttribute("width", "1");
		dropShadow.setAttribute("height", "1");
		dropShadow.setAttribute("filterMarginsUnits", "userSpaceOnUse");
		dropShadow.setAttribute("dx", "0");
		dropShadow.setAttribute("dy", "0");
		dropShadow.setAttribute("dw", "5");
		dropShadow.setAttribute("dh", "5");

		Element gaussianBlur = (Element) document
				.createElement("feGaussianBlur");
		gaussianBlur.setAttribute("stdDeviation", String.valueOf(fuzziness));
		gaussianBlur.setAttribute("in", "SourceAlpha");
		dropShadow.appendChild(gaussianBlur);

		Element offset = (Element) document.createElement("feOffset");
		offset.setAttribute("dx", String.valueOf(dx));
		offset.setAttribute("dy", String.valueOf(dy));
		dropShadow.appendChild(offset);

		Element merge = (Element) document.createElement("feMerge");
		merge.appendChild(document.createElement("feMergeNode"));
		Element mergeNode = (Element) document.createElement("feMergeNode");
		mergeNode.setAttribute("in", "SourceGraphic");
		merge.appendChild(mergeNode);
		dropShadow.appendChild(merge);

		return dropShadow;
	}

	/**
	 * Returns the labels for a graph cell as an object array.
	 * 
	 * @param view
	 * @return the labels displayed for the specified cell view
	 */
	public Object[] getLabels(CellView view) {
//		LabelUserObject user = (LabelUserObject) ((DefaultMutableTreeNode) view
//				.getCell()).getUserObject();
//		return user.getTspanElements().toArray();
		return new Object[0];
	}

}
