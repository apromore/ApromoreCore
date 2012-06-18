package org.apromore.canoniser.adapters.canonical2pnml;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.GraphicsType;
import org.apromore.pnml.AnnotationGraphisType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.DimensionType;
import org.apromore.pnml.Fill;
import org.apromore.pnml.Font;
import org.apromore.pnml.GraphicsArcType;
import org.apromore.pnml.GraphicsNodeType;
import org.apromore.pnml.Line;
import org.apromore.pnml.NodeNameType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.PositionType;
import org.apromore.pnml.TransitionResourceType;
import org.apromore.pnml.TransitionType;
import org.apromore.pnml.TriggerType;

import java.math.BigDecimal;

public class TranslateNodeAnnotations {
	DataHandler data;

	public void setValue(DataHandler data) {
		this.data = data;
	}

	public void translate(AnnotationType annotation, String cid) {
		GraphicsType cGraphInfo = (GraphicsType) annotation;
		GraphicsNodeType graphics = new GraphicsNodeType();
		GraphicsArcType lines = new GraphicsArcType();
		AnnotationGraphisType annograph = new AnnotationGraphisType();
		org.apromore.pnml.PositionType pos1 = new org.apromore.pnml.PositionType();

		if (cGraphInfo.getFill() != null) {
			Fill fill = new Fill();
			fill.setColor(cGraphInfo.getFill().getColor());
			fill.setGradientColor(cGraphInfo.getFill().getGradientColor());
			fill.setGradientRotation(cGraphInfo.getFill().getGradientRotation());
			fill.setImages(cGraphInfo.getFill().getImage());
			graphics.setFill(fill);
		}
		if (cGraphInfo.getFont() != null) {
			Font font = new Font();
			font.setDecoration(cGraphInfo.getFont().getDecoration());
			font.setFamily(cGraphInfo.getFont().getFamily());
			font.setAlign(cGraphInfo.getFont().getHorizontalAlign());
			font.setRotation(cGraphInfo.getFont().getRotation());
			font.setSize(String.valueOf(cGraphInfo.getFont().getSize()));
			font.setStyle(cGraphInfo.getFont().getStyle());
			font.setWeight(cGraphInfo.getFont().getWeight());
			pos1.setX(cGraphInfo.getFont().getXPosition());
			pos1.setY(cGraphInfo.getFont().getYPosition());
		}
		if (cGraphInfo.getLine() != null) {
			Line line = new Line();
			line.setColor(cGraphInfo.getLine().getColor());
			line.setShape(cGraphInfo.getLine().getShape());
			line.setStyle(cGraphInfo.getLine().getStyle());
			line.setWidth(cGraphInfo.getLine().getWidth());
			lines.setLine(line);
		}
		org.apromore.pnml.PositionType pos = new org.apromore.pnml.PositionType();
		DimensionType dim = new DimensionType();
		if (cGraphInfo.getSize() != null) {
			if (data.getInitialType().equals("PNML")) {
				dim.setX(cGraphInfo.getSize().getWidth());
				dim.setY(cGraphInfo.getSize().getHeight());
			} else {
				dim.setX(BigDecimal.valueOf(Long.valueOf(40)));
				dim.setY(BigDecimal.valueOf(Long.valueOf(40)));
			}
		}
		if (cGraphInfo.getPosition() != null
				&& cGraphInfo.getPosition().size() > 0) {

			pos.setX(cGraphInfo.getPosition().get(0).getX());
			pos.setY(cGraphInfo.getPosition().get(0).getY());
		}
		graphics.setPosition(pos);
		graphics.setDimension(dim);

		Object obj = data.get_pnmlRefMap_value(data.get_id_map_value(cid));
		if (obj instanceof PlaceType) {

			NodeNameType nnt = new NodeNameType();

			if (((PlaceType) obj).getName() != null) {
				if (pos1.getX() != null && pos1.getY() != null) {
					annograph.getOffsetAndFillAndLine().add(pos1);
					nnt.setGraphics(annograph);
				}
				nnt.setText(((PlaceType) obj).getName().getText());
				((PlaceType) obj).setName(nnt);
			}
			((PlaceType) obj).setGraphics(graphics);
		} else if (obj instanceof TransitionType) {
			NodeNameType nnt = new NodeNameType();
			if (((TransitionType) obj).getName() != null) {
				if (pos1.getX() != null && pos1.getY() != null) {
					annograph.getOffsetAndFillAndLine().add(pos1);

					nnt.setGraphics(annograph);
				}
				nnt.setText(((TransitionType) obj).getName().getText());
				((TransitionType) obj).setName(nnt);
			}
			((TransitionType) obj).setGraphics(graphics);
			if (data.get_triggermap().containsKey(
					((TransitionType) obj).getName().getText())) {
				TriggerType tt = (data
						.get_triggermap_value(((TransitionType) obj).getName()
								.getText()));
				PositionType pt = new PositionType();
				pt.setX((((TransitionType) obj).getGraphics().getPosition()
						.getX().add(BigDecimal.valueOf(Long.valueOf(10)))));
				pt.setY((((TransitionType) obj).getGraphics().getPosition()
						.getY().subtract(BigDecimal.valueOf(Long.valueOf(20)))));
				tt.getGraphics().setPosition(pt);
			}
			if (data.get_resourcepositionmap().containsKey(
					((TransitionType) obj).getName().getText())) {
				TransitionResourceType tres = (data
						.get_resourcepositionmap_value(((TransitionType) obj)
								.getName().getText()));
				PositionType pt = new PositionType();
				pt.setX((((TransitionType) obj).getGraphics().getPosition()
						.getX().subtract(BigDecimal.valueOf(Long.valueOf(10)))));
				pt.setY((((TransitionType) obj).getGraphics().getPosition()
						.getY().subtract(BigDecimal.valueOf(Long.valueOf(47)))));
				tres.getGraphics().setPosition(pt);
			}

		} else if (obj instanceof ArcType) {

			if (cGraphInfo.getPosition() != null
					&& cGraphInfo.getPosition().size() > 0)
				for (int i = 0; i < cGraphInfo.getPosition().size(); i++) {
					org.apromore.pnml.PositionType pos2 = new org.apromore.pnml.PositionType();

					pos2.setX(cGraphInfo.getPosition().get(i).getX());
					pos2.setY(cGraphInfo.getPosition().get(i).getY());

					lines.getPosition().add(pos2);
				}
			((ArcType) obj).setGraphics(lines);

		}

	}

}
