/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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