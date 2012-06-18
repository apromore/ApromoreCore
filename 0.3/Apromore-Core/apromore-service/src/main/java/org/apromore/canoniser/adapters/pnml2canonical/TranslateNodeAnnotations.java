package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.anf.CpfTypeEnum;
import org.apromore.anf.FillType;
import org.apromore.anf.FontType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.LineType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SimulationType;
import org.apromore.anf.SizeType;
import org.apromore.pnml.Fill;
import org.apromore.pnml.PlaceType;

import java.math.BigInteger;
import java.util.List;

public class TranslateNodeAnnotations {
	DataHandler data;

	public void setValues(DataHandler data) {
		this.data = data;
	}

	public void addNodeAnnotations(Object obj) {
		GraphicsType graphT = new GraphicsType();
		LineType line = new LineType();
		FillType fill = new FillType();
		PositionType pos = new PositionType();
		SizeType size = new SizeType();
		FontType font = new FontType();
		String cpfId;

		org.apromore.pnml.NodeType element = (org.apromore.pnml.NodeType) obj;

		cpfId = data.get_id_map_value(element.getId());

		if (element.getGraphics() != null) {
			if (element.getName() != null) {
				if (element.getName().getGraphics() != null) {
					List<Object> agt = element.getName().getGraphics().getOffsetAndFillAndLine();
					for (Object oaf : agt) {
						if (oaf instanceof Fill) {
							if (((Fill) oaf).getImages() != null) {
								fill.setImage(((Fill) oaf).getImages());
							}
							if (((Fill) oaf).getColor() != null) {
								fill.setColor(((Fill) oaf).getColor());
							}
							if (((Fill) oaf).getGradientColor() != null) {
								fill.setGradientColor(((Fill) oaf).getGradientColor());
							}
							if (((Fill) oaf).getGradientRotation() != null) {
								fill.setGradientRotation(((Fill) oaf).getGradientRotation());
							}
							graphT.setFill(fill);
						}

						else if (oaf instanceof org.apromore.pnml.Font) {
							if (((org.apromore.pnml.Font) oaf).getFamily() != null) {
								font.setFamily(((org.apromore.pnml.Font) oaf).getFamily());
							}
							if (((org.apromore.pnml.Font) oaf).getStyle() != null) {
								font.setStyle(((org.apromore.pnml.Font) oaf).getStyle());
							}
							if (((org.apromore.pnml.Font) oaf).getWeight() != null) {
								font.setWeight(((org.apromore.pnml.Font) oaf).getWeight());
							}
							if (((org.apromore.pnml.Font) oaf).getSize() != null) {
								font.setSize(BigInteger.valueOf(Long.valueOf(((org.apromore.pnml.Font) oaf).getSize())));
							}
							if (((org.apromore.pnml.Font) oaf).getDecoration() != null) {
								font.setDecoration(((org.apromore.pnml.Font) oaf).getDecoration());
							}
							if (((org.apromore.pnml.Font) oaf).getRotation() != null) {
								font.setRotation(((org.apromore.pnml.Font) oaf).getRotation());
							}
							graphT.setFont(font);
						} else if (oaf instanceof org.apromore.pnml.PositionType) {
							if (graphT.getFont() != null) {
								font = graphT.getFont();
							}
							if (((org.apromore.pnml.PositionType) oaf).getX() != null) {
								font.setXPosition(((org.apromore.pnml.PositionType) oaf).getX());
							}
							if (((org.apromore.pnml.PositionType) oaf).getY() != null) {
								font.setYPosition(((org.apromore.pnml.PositionType) oaf).getY());
							}
							graphT.setFont(font);
						}
					}

				}
			}
			if (element.getGraphics() != null) {
				size.setHeight(element.getGraphics().getDimension().getX());
				size.setWidth(element.getGraphics().getDimension().getY());
				graphT.setSize(size);

				pos.setX(element.getGraphics().getPosition().getX());
				pos.setY(element.getGraphics().getPosition().getY());
				graphT.getPosition().add(pos);
			}

			if (element.getGraphics().getLine() != null) {
				line.setColor(element.getGraphics().getLine().getColor());
				line.setShape(element.getGraphics().getLine().getShape());
				line.setStyle(element.getGraphics().getLine().getStyle());
				line.setWidth(element.getGraphics().getLine().getWidth());
				graphT.setLine(line);
			}

			graphT.setCpfId(cpfId);
			data.getAnnotations().getAnnotation().add(graphT);

		}
		if (element instanceof PlaceType) {
			SimulationType simu = new SimulationType();
			if (((PlaceType) element).getInitialMarking() != null) {
				if (((PlaceType) element).getInitialMarking().getText() != null) {
					simu.setInitialMarking(((PlaceType) element).getInitialMarking().getText());
					simu.setCpfId(cpfId);
					simu.setCpfType(CpfTypeEnum.fromValue("WorkType"));
					data.getAnnotations().getAnnotation().add(simu);
				}
			}

		}

	}

}
