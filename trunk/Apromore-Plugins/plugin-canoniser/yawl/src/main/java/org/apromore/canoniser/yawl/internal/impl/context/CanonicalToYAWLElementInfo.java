package org.apromore.canoniser.yawl.internal.impl.context;

import org.yawlfoundation.yawlschema.ControlTypeType;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.LayoutRectangleType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType.YawlService;

public final class CanonicalToYAWLElementInfo {

    private ExternalNetElementType element;
    private LayoutRectangleType elementSize;
    private ControlTypeType joinType;
    private ControlTypeType splitType;
    private org.yawlfoundation.yawlschema.TimerType timer;
    private boolean isAutomatic = false;
    private YawlService yawlService;
    private NetFactsType parent;

    public ExternalNetElementType getElement() {
        return element;
    }

    public void setElement(final ExternalNetElementType element) {
        this.element = element;
    }

    public LayoutRectangleType getElementSize() {
        return elementSize;
    }

    public void setElementSize(final LayoutRectangleType elementSize) {
        this.elementSize = elementSize;
    }

    public ControlTypeType getJoinType() {
        return joinType;
    }

    public void setJoinType(final ControlTypeType joinType) {
        this.joinType = joinType;
    }

    public ControlTypeType getSplitType() {
        return splitType;
    }

    public void setSplitType(final ControlTypeType splitType) {
        this.splitType = splitType;
    }

    public org.yawlfoundation.yawlschema.TimerType getTimer() {
        return timer;
    }

    public void setTimer(final org.yawlfoundation.yawlschema.TimerType timer) {
        this.timer = timer;
    }

    public boolean isAutomatic() {
        return isAutomatic;
    }

    public void setAutomatic(final boolean isAutomatic) {
        this.isAutomatic = isAutomatic;
    }

    public YawlService getYawlService() {
        return yawlService;
    }

    public void setYawlService(final YawlService yawlService) {
        this.yawlService = yawlService;
    }

    public NetFactsType getParent() {
        return parent;
    }

    public void setParent(final NetFactsType parent) {
        this.parent = parent;
    }
}