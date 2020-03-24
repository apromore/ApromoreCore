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

public class CallActivity extends BPMNNode implements Decorated {

    protected boolean bLooped = false;
    protected boolean bAdhoc = false;
    protected boolean bCompensation = false;
    protected boolean bMultiinstance = false;
    protected boolean bCollapsed = false;
    public static final int PADDINGFROMBOXTOTEXT = 3;
    public static final int BRANCHINGBOXWIDTH = 15;
    protected final static int stdWidth = 80;
    protected final static int stdHeight = 40;
    private IGraphElementDecoration decorator = null;
    private int numOfBoundaryEvents = 0;

    public CallActivity(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
                    String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                    boolean bCollapsed) {
        super(bpmndiagram);
        fillAttributes(label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed);
    }

    public CallActivity(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
                    String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                    boolean bCollapsed, SubProcess parentSubProcess) {
        super(bpmndiagram, parentSubProcess);
        fillAttributes(label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed);
    }

    public CallActivity(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
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
        getAttributeMap().put(AttributeMap.BORDERWIDTH,3);
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
