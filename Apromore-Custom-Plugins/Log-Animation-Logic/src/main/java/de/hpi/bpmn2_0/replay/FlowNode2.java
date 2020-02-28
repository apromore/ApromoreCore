/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.FlowNode;

public class FlowNode2 extends FlowNode {
    private Object nameRef = null;
    
    public FlowNode2(FlowNode flowNode) {
        super(flowNode);
    }
    
    public Object getNameRef() {
        return nameRef;
    }
    
    public void setNameRef(Object nameRef) {
        this.nameRef = nameRef;
    }
}