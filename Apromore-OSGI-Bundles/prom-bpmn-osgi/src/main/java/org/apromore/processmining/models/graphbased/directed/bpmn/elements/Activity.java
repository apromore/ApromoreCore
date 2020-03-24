package org.apromore.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.shapes.Decorated;
import org.apromore.processmining.models.shapes.Rectangle;

public class Activity extends BPMNNode implements Decorated {

	protected boolean bLooped = false;
	protected boolean bAdhoc = false;
	protected boolean bCompensation = false;
	protected boolean bMultiinstance = false;
	protected boolean bCollapsed = false;
	protected boolean bReceive = false;
	protected boolean bSend = false;
	private boolean bService = false;
	private boolean bScript = false;
	public static final int PADDINGFROMBOXTOTEXT = 3;
	public static final int BRANCHINGBOXWIDTH = 15;
	protected final static int stdWidth = 100;
	protected final static int stdHeight = 40;
	private IGraphElementDecoration decorator = null;
	private int numOfBoundaryEvents = 0;
	
	public Activity(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed) {
		super(bpmndiagram);
		fillAttributes(label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed);
	}

	public Activity(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed, SubProcess parentSubProcess) {
		super(bpmndiagram, parentSubProcess);
		fillAttributes(label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed);
	}

	public Activity(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed, Swimlane parentSwimlane) {
		super(bpmndiagram, parentSwimlane);
		fillAttributes(label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed);
	}

	public IGraphElementDecoration getDecorator() {
		return decorator;
	}

	public void setDecorator(IGraphElementDecoration decorator) {
		this.decorator = decorator;
	}

	/**
	 * @param label
	 * @param bLooped
	 * @param bAdhoc
	 * @param bCompensation
	 * @param bMultiinstance
	 * @param bCollapsed
	 */
	private void fillAttributes(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed) {
		this.bLooped = bLooped;
		this.bAdhoc = bAdhoc;
		this.bCompensation = bCompensation;
		this.bMultiinstance = bMultiinstance;
		this.bCollapsed = bCollapsed;
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(stdWidth, stdHeight));
		getAttributeMap().put(AttributeMap.SHAPE, new Rectangle(true));
		getAttributeMap().put(AttributeMap.SQUAREBB, false);
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
	}

	
	
	public boolean isBScript() {
		return bScript;
	}

	public void setBScript(boolean BScript) {
		this.bScript = BScript;
	}

	public boolean isBService() {
		return bService;
	}

	public void setBService(boolean BService) {
		this.bService = BService;
	}

	public boolean isBReceive() {
		return bReceive;
	}

	public void setBReceive(boolean receive) {
		this.bReceive = receive;
	}

	public boolean isBSend() {
		return bSend;
	}

	public void setBSend(boolean send) {
		this.bSend = send;
	}

	public boolean isBCollapsed() {
		return bCollapsed;
	}

	public void setBCollapsed(boolean collapsed) {
		bCollapsed = collapsed;
	}

	public boolean isBLooped() {
		return bLooped;
	}

	public void setBLooped(boolean looped) {
		bLooped = looped;
	}

	public boolean isBAdhoc() {
		return bAdhoc;
	}

	public void setBAdhoc(boolean adhoc) {
		bAdhoc = adhoc;
	}

	public boolean isBCompensation() {
		return bCompensation;
	}

	public void setBCompensation(boolean compensation) {
		bCompensation = compensation;
	}

	public boolean isBMultiinstance() {
		return bMultiinstance;
	}

	public void setBMultiinstance(boolean multiinstance) {
		bMultiinstance = multiinstance;
	}

	public Swimlane getParentSwimlane() {
		if (getParent() != null) {
			if (getParent() instanceof Swimlane)
				return (Swimlane) getParent();
			else
				return null;
		}
		return null;
	}

	public SubProcess getParentSubProcess() {
		if (getParent() != null) {
			if (getParent() instanceof SubProcess)
				return (SubProcess) getParent();
			else
				return null;
		}
		return null;
	}

	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		if (decorator != null) {
			decorator.decorate(g2d, x, y, width, height);
			final int labelX = (int) Math.round(x + BRANCHINGBOXWIDTH + (2 * PADDINGFROMBOXTOTEXT) + 10);
			final int labelY = (int) Math.round(y);
			final int labelW = (int) Math.round(width - 2 * (BRANCHINGBOXWIDTH + (2 * PADDINGFROMBOXTOTEXT) + 10));
			final int labelH = (int) Math.round(height-20);

			JLabel label = new JLabel(getLabel());
			label.setPreferredSize(new Dimension(labelW, labelH));
			label.setSize(new Dimension(labelW, labelH));

			label.setFont(new Font(label.getFont().getFamily(), label.getFont().getStyle(), 8));
			label.validate();
			label.paint(g2d.create(labelX, labelY, labelW, labelH));
		} else {

			int nrDecorators = 0;

			GeneralPath activityDecorator = new GeneralPath();

			if (bLooped) {
				activityDecorator.moveTo(nrDecorators * 12 + 4.5F, 7);
				activityDecorator.lineTo(nrDecorators * 12 + 4.5F, 10);
				activityDecorator.lineTo(nrDecorators * 12 + 1.5F, 10);
				activityDecorator.append(new Arc2D.Float(nrDecorators * 12, 0, 10, 10, 260, -310, Arc2D.OPEN), false);

				nrDecorators++;
			}
			if (bAdhoc) {
				activityDecorator.append(new Arc2D.Float(nrDecorators * 12, 2, 5, 6, 180, -180, Arc2D.OPEN), false);
				activityDecorator.append(new Arc2D.Float(nrDecorators * 12 + 5, 2, 5, 6, 180, 180, Arc2D.OPEN), true);

				nrDecorators++;
			}
			if (bCompensation) {
				activityDecorator.moveTo(nrDecorators * 12 + 6, 5);
				activityDecorator.lineTo(nrDecorators * 12 + 11, 0);
				activityDecorator.lineTo(nrDecorators * 12 + 11, 10);
				activityDecorator.closePath();

				activityDecorator.moveTo(nrDecorators * 12, 5);
				activityDecorator.lineTo(nrDecorators * 12 + 5, 10);
				activityDecorator.lineTo(nrDecorators * 12 + 5, 0);
				activityDecorator.closePath();

				nrDecorators++;
			}
			if (bMultiinstance) {
				activityDecorator.moveTo(nrDecorators * 12 + 3, 0);
				activityDecorator.lineTo(nrDecorators * 12 + 3, 10);
				activityDecorator.moveTo(nrDecorators * 12 + 6, 0);
				activityDecorator.lineTo(nrDecorators * 12 + 6, 10);
				activityDecorator.moveTo(nrDecorators * 12 + 9, 0);
				activityDecorator.lineTo(nrDecorators * 12 + 9, 10);

				nrDecorators++;
			}
			if (bCollapsed) {
				activityDecorator.append(new Rectangle2D.Float(nrDecorators * 12 + 1, 0, 10, 10), false);
				activityDecorator.moveTo(nrDecorators * 12 + 6, 0);
				activityDecorator.lineTo(nrDecorators * 12 + 6, 10);
				activityDecorator.moveTo(nrDecorators * 12 + 1, 5);
				activityDecorator.lineTo(nrDecorators * 12 + 11, 5);
				nrDecorators++;
			}


			AffineTransform at = new AffineTransform();
			at.translate(width / 2 - Math.round(nrDecorators / 2) * 12, height - 13);
			activityDecorator.transform(at);

			at = new AffineTransform();
			at.translate(x, y);
			activityDecorator.transform(at);

			g2d.draw(activityDecorator);

			if(bReceive){
				activityDecorator = new GeneralPath();
				activityDecorator.moveTo(2, 2);
				activityDecorator.lineTo(2, 11);
				activityDecorator.lineTo(18, 11);
				activityDecorator.lineTo(18, 2);
				activityDecorator.closePath();

				activityDecorator.moveTo(3, 3);
				activityDecorator.lineTo(10, 8);
				activityDecorator.lineTo(18, 2);

				g2d.draw(activityDecorator);


			}else if(bSend){
				activityDecorator = new GeneralPath();
				activityDecorator.moveTo(2, 2);
				activityDecorator.lineTo(2, 11);
				activityDecorator.lineTo(18, 11);
				activityDecorator.lineTo(18, 2);
				activityDecorator.lineTo(10, 8);
				activityDecorator.lineTo(2, 2);

				g2d.fill(activityDecorator);
				activityDecorator = new GeneralPath();
				activityDecorator.moveTo(3, 2);
				activityDecorator.lineTo(10, 7);
				activityDecorator.lineTo(17, 2);
				activityDecorator.lineTo(3, 2);

				g2d.fill(activityDecorator);



			} else if (bScript) {
				// HV: Add script decorator.
				activityDecorator = new GeneralPath();
				activityDecorator.moveTo(6, 6);
				activityDecorator.lineTo(6, 26);
				activityDecorator.lineTo(22, 26);
				activityDecorator.lineTo(22, 6);
				activityDecorator.lineTo(6, 6);
				for (int line = 10; line < 26; line += 4) {
					activityDecorator.moveTo(10, line);
					activityDecorator.lineTo(18, line);
				}
		        AffineTransform scaleup = new AffineTransform();
	            scaleup.scale(0.5, 0.5);
	            activityDecorator.transform(scaleup);
		g2d.draw(activityDecorator);
				
			} else if(bService){
				activityDecorator = new GeneralPath();
				 
		        activityDecorator.moveTo(113.595, 133.642);
		        activityDecorator.lineTo(107.663, 120.473);
		        activityDecorator.curveTo(113.318, 116.322, 118.175, 111.158, 121.97, 105.264);
		        activityDecorator.lineTo(135.477, 110.382);
		        activityDecorator.curveTo(138.06, 111.361, 140.946, 110.060005, 141.92401, 107.478004);
		        activityDecorator.lineTo(146.88802, 94.37501);
		        activityDecorator.curveTo(147.35802, 93.13501, 147.31601, 91.75901, 146.77101, 90.55001);
		        activityDecorator.curveTo(146.22601, 89.34101, 145.22401, 88.39801, 143.98302, 87.92801);
		        activityDecorator.lineTo(130.47601, 82.81001);
		        activityDecorator.curveTo(131.54001, 75.88001, 131.32402, 68.79601, 129.83902, 61.93901);
		        activityDecorator.lineTo(143.00803, 56.00701);
		        activityDecorator.curveTo(144.21703, 55.462013, 145.16002, 54.46001, 145.63002, 53.21901);
		        activityDecorator.curveTo(146.10002, 51.979008, 146.05801, 50.60301, 145.51302, 49.39401);
		        activityDecorator.lineTo(139.75801, 36.61901);
		        activityDecorator.curveTo(138.62401, 34.10101, 135.66202, 32.98101, 133.14601, 34.11401);
		        activityDecorator.lineTo(119.97701, 40.04601);
		        activityDecorator.curveTo(115.82601, 34.39101, 110.66201, 29.534008, 104.76801, 25.73901);
		        activityDecorator.lineTo(109.88602, 12.23201);
		        activityDecorator.curveTo(110.86401, 9.65001, 109.56402, 6.76301, 106.98202, 5.78501);
		        activityDecorator.lineTo(93.88, 0.82);
		        activityDecorator.curveTo(92.641, 0.35099998, 91.265, 0.392, 90.055, 0.937);
		        activityDecorator.curveTo(88.846, 1.482, 87.903, 2.484, 87.433, 3.7250001);
		        activityDecorator.lineTo(82.316, 17.230999);
		        activityDecorator.curveTo(75.379005, 16.161, 68.283005, 16.382, 61.444, 17.866999);
		        activityDecorator.lineTo(55.513, 4.699);
		        activityDecorator.curveTo(54.968002, 3.4899998, 53.966, 2.547, 52.725, 2.077);
		        activityDecorator.curveTo(51.486, 1.6079999, 50.108997, 1.6489999, 48.899998, 2.194);
		        activityDecorator.lineTo(36.124, 7.949);
		        activityDecorator.curveTo(33.606, 9.083, 32.485, 12.042999, 33.619, 14.561);
		        activityDecorator.lineTo(39.551, 27.73);
		        activityDecorator.curveTo(33.896, 31.881, 29.038998, 37.045, 25.244, 42.939);
		        activityDecorator.lineTo(11.7369995, 37.821);
		        activityDecorator.curveTo(10.497999, 37.351997, 9.122, 37.394, 7.9119997, 37.938);
		        activityDecorator.curveTo(6.7029996, 38.482998, 5.7599998, 39.485, 5.29, 40.725998);
		        activityDecorator.lineTo(0.326, 53.828);
		        activityDecorator.curveTo(-0.65199995, 56.41, 0.648, 59.296997, 3.23, 60.274998);
		        activityDecorator.lineTo(16.737, 65.393);
		        activityDecorator.curveTo(15.672999, 72.322, 15.889, 79.408, 17.373999, 86.264);
		        activityDecorator.lineTo(4.204, 92.196);
		        activityDecorator.curveTo(2.995, 92.741, 2.052, 93.743, 1.582, 94.984);
		        activityDecorator.curveTo(1.112, 96.224, 1.154, 97.6, 1.699, 98.809);
		        activityDecorator.lineTo(7.454, 111.584);
		        activityDecorator.curveTo(7.998, 112.793, 9.001, 113.736, 10.241, 114.206);
		        activityDecorator.curveTo(11.482, 114.676, 12.857, 114.635, 14.066, 114.089005);
		        activityDecorator.lineTo(27.235, 108.157005);
		        activityDecorator.curveTo(31.386002, 113.813, 36.549, 118.66901, 42.444, 122.464005);
		        activityDecorator.lineTo(37.326, 135.97101);
		        activityDecorator.curveTo(36.348, 138.55301, 37.648, 141.44, 40.23, 142.41801);
		        activityDecorator.lineTo(53.333, 147.38202);
		        activityDecorator.curveTo(53.904, 147.59802, 54.505, 147.70602, 55.104, 147.70602);
		        activityDecorator.curveTo(55.805, 147.70602, 56.506, 147.55902, 57.158, 147.26503);
		        activityDecorator.curveTo(58.367, 146.72003, 59.31, 145.71803, 59.780003, 144.47704);
		        activityDecorator.lineTo(64.897, 130.97104);
		        activityDecorator.curveTo(71.834, 132.04004, 78.931, 131.82004, 85.769005, 130.33504);
		        activityDecorator.lineTo(91.700005, 143.50304);
		        activityDecorator.curveTo(92.245, 144.71204, 93.247, 145.65503, 94.48801, 146.12503);
		        activityDecorator.curveTo(95.728004, 146.59503, 97.105, 146.55403, 98.313, 146.00803);
		        activityDecorator.lineTo(111.088005, 140.25403);
		        activityDecorator.curveTo(113.607, 139.12, 114.729, 136.16, 113.595, 133.642);
		        activityDecorator.closePath();
		        activityDecorator.moveTo(105.309, 86.113);
		        activityDecorator.curveTo(100.346, 99.213, 87.603, 108.014, 73.6, 108.014);
		        activityDecorator.curveTo(69.504, 108.014, 65.465, 107.27, 61.594997, 105.804);
		        activityDecorator.curveTo(53.127, 102.596, 46.414997, 96.282, 42.696, 88.025);
		        activityDecorator.curveTo(38.976997, 79.769005, 38.696, 70.558, 41.904, 62.090004);
		        activityDecorator.curveTo(46.867, 48.990005, 59.61, 40.189003, 73.613, 40.189003);
		        activityDecorator.curveTo(77.709, 40.189003, 81.748, 40.933002, 85.618, 42.399002);
		        activityDecorator.curveTo(94.086, 45.607002, 100.798, 51.921, 104.517, 60.177002);
		        activityDecorator.curveTo(108.237, 68.434, 108.518, 77.645, 105.309, 86.113);
		        activityDecorator.closePath();
		        activityDecorator.moveTo(216.478, 154.389);
		        activityDecorator.curveTo(215.582, 153.412, 214.333, 152.83101, 213.009, 152.774);
		        activityDecorator.lineTo(203.591, 152.37);
		        activityDecorator.curveTo(202.724, 147.92499, 201.158, 143.634, 198.95801, 139.67299);
		        activityDecorator.lineTo(205.90302, 133.299);
		        activityDecorator.curveTo(207.93802, 131.43199, 208.07301, 128.269, 206.20601, 126.23499);
		        activityDecorator.lineTo(199.31001, 118.72099);
		        activityDecorator.curveTo(198.41402, 117.743996, 197.16501, 117.162994, 195.84001, 117.105995);
		        activityDecorator.curveTo(194.518, 117.05699, 193.22202, 117.521996, 192.24501, 118.41799);
		        activityDecorator.lineTo(185.30101, 124.79199);
		        activityDecorator.curveTo(181.542, 122.260994, 177.40102, 120.33399, 173.04701, 119.08999);
		        activityDecorator.lineTo(173.45102, 109.67199);
		        activityDecorator.curveTo(173.56902, 106.91299, 171.42802, 104.580986, 168.66902, 104.46299);
		        activityDecorator.lineTo(158.48003, 104.02599);
		        activityDecorator.curveTo(155.73503, 103.922, 153.38902, 106.048996, 153.27103, 108.80699);
		        activityDecorator.lineTo(152.86702, 118.22499);
		        activityDecorator.curveTo(148.42302, 119.09199, 144.13202, 120.65799, 140.17001, 122.856995);
		        activityDecorator.lineTo(133.79602, 115.911995);
		        activityDecorator.curveTo(132.90002, 114.935, 131.65102, 114.354, 130.32703, 114.297);
		        activityDecorator.curveTo(129.00302, 114.243, 127.70903, 114.713, 126.732025, 115.60899);
		        activityDecorator.lineTo(119.218025, 122.50499);
		        activityDecorator.curveTo(117.18302, 124.37199, 117.04803, 127.53499, 118.91502, 129.56898);
		        activityDecorator.lineTo(125.289024, 136.51399);
		        activityDecorator.curveTo(122.758026, 140.273, 120.831024, 144.413, 119.58702, 148.76799);
		        activityDecorator.lineTo(110.17002, 148.36398);
		        activityDecorator.curveTo(107.42302, 148.25299, 105.07802, 150.38599, 104.96002, 153.14499);
		        activityDecorator.lineTo(104.523026, 163.33398);
		        activityDecorator.curveTo(104.46603, 164.65898, 104.93803, 165.95198, 105.83502, 166.92899);
		        activityDecorator.curveTo(106.731026, 167.90599, 107.98002, 168.48698, 109.30502, 168.54399);
		        activityDecorator.lineTo(118.72202, 168.94699);
		        activityDecorator.curveTo(119.58902, 173.392, 121.15502, 177.68298, 123.35402, 181.64499);
		        activityDecorator.lineTo(116.41002, 188.01898);
		        activityDecorator.curveTo(115.43302, 188.91498, 114.85202, 190.16399, 114.79502, 191.48798);
		        activityDecorator.curveTo(114.73802, 192.81297, 115.21002, 194.10597, 116.10702, 195.08298);
		        activityDecorator.lineTo(123.00302, 202.59698);
		        activityDecorator.curveTo(123.899025, 203.57399, 125.14802, 204.15498, 126.47302, 204.21199);
		        activityDecorator.curveTo(127.79202, 204.26498, 129.09102, 203.79599, 130.06802, 202.9);
		        activityDecorator.lineTo(137.01202, 196.526);
		        activityDecorator.curveTo(140.77103, 199.057, 144.91202, 200.98401, 149.26602, 202.228);
		        activityDecorator.lineTo(148.86201, 211.646);
		        activityDecorator.curveTo(148.74402, 214.405, 150.88402, 216.737, 153.64302, 216.855);
		        activityDecorator.lineTo(163.83202, 217.29199);
		        activityDecorator.curveTo(163.90402, 217.295, 163.97502, 217.29599, 164.04602, 217.29599);
		        activityDecorator.curveTo(165.29602, 217.29599, 166.50302, 216.82799, 167.42702, 215.98);
		        activityDecorator.curveTo(168.40402, 215.084, 168.98502, 213.83499, 169.04202, 212.511);
		        activityDecorator.lineTo(169.44603, 203.093);
		        activityDecorator.curveTo(173.89003, 202.226, 178.18103, 200.66, 182.14304, 198.461);
		        activityDecorator.lineTo(188.51703, 205.406);
		        activityDecorator.curveTo(189.41302, 206.38301, 190.66203, 206.964, 191.98602, 207.02101);
		        activityDecorator.curveTo(193.31602, 207.07901, 194.60503, 206.60501, 195.58102, 205.70901);
		        activityDecorator.lineTo(203.09503, 198.81302);
		        activityDecorator.curveTo(205.13004, 196.94601, 205.26503, 193.78302, 203.39803, 191.74902);
		        activityDecorator.lineTo(197.02403, 184.80402);
		        activityDecorator.curveTo(199.55504, 181.04501, 201.48203, 176.90501, 202.72603, 172.55002);
		        activityDecorator.lineTo(212.14304, 172.95403);
		        activityDecorator.curveTo(214.89903, 173.06003, 217.23404, 170.93202, 217.35304, 168.17302);
		        activityDecorator.lineTo(217.79004, 157.98402);
		        activityDecorator.curveTo(217.847, 156.659, 217.375, 155.366, 216.478, 154.389);
		        activityDecorator.closePath();
		        activityDecorator.moveTo(160.157, 183.953);
		        activityDecorator.curveTo(147.313, 183.403, 137.311, 172.505, 137.862, 159.66101);
		        activityDecorator.curveTo(138.398, 147.147, 148.621, 137.34401, 161.135, 137.34401);
		        activityDecorator.curveTo(161.47299, 137.34401, 161.81299, 137.35101, 162.15399, 137.36601);
		        activityDecorator.curveTo(174.99799, 137.917, 185.0, 148.81401, 184.44899, 161.65802);
		        activityDecorator.curveTo(183.898, 174.511, 173.106, 184.497, 160.157, 183.953);
		        activityDecorator.closePath();

		        g2d.setPaint(java.awt.Color.BLACK);
		        
		        AffineTransform scaleup = new AffineTransform();
	              // scaleup.setToScale(2, 2);
	               scaleup.scale(0.07, 0.07);
	               activityDecorator.transform(scaleup);
		        g2d.fill(activityDecorator);
			}

			// BVD: Added this code in order to correctly render HTML. 
			//
			// The label should be in a box from 
			// - Math.round(x + BRANCHINGBOXWIDTH + (2 * PADDINGFROMBOXTOTEXT) + 10), y to
			// - Math.round(x + width - (BRANCHINGBOXWIDTH + (2 * PADDINGFROMBOXTOTEXT) + 10), y+height)

			final int labelX = (int) Math.round(x + 10);
			final int labelY = (int) Math.round(y+10);
			final int labelW = (int) Math.round(width-10);
			final int labelH = (int) Math.round(height-10);

			JLabel label = new JLabel(getLabel());
			label.setPreferredSize(new Dimension(labelW, labelH));
			label.setSize(new Dimension(labelW, labelH));

			label.setFont(new Font(label.getFont().getFamily(), label.getFont().getStyle(), 12));
			label.validate();
			label.paint(g2d.create(labelX, labelY, labelW, labelH));
		}
	}

	public int getNumOfBoundaryEvents() {
		return numOfBoundaryEvents;
	}

	public void incNumOfBoundaryEvents() {
		numOfBoundaryEvents++;
	}
}
