/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package de.hpi.bpmn2_0.model.conversation;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing a conversation link.
 *
 * @author Sven Wagner-Boysen
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConversationLink extends Edge implements ConversationElement {

    public void acceptVisitor(Visitor v) {
        v.visitConversationLink(this);
    }

    /**
     * Ensures that the target element is of the type conversation element and
     * returns it.
     */
    public FlowElement getTargetRef() {
//		if(!(super.getTargetRef() instanceof ConversationElement))
//			return null;
        return super.getTargetRef();
    }

    public void setTargetRef(ConversationElement targetEle) {
        if (targetEle instanceof FlowElement) {
            super.setTargetRef((FlowElement) targetEle);
        }
    }

    /**
     * Ensures that the source element is of the type conversation element and
     * returns it.
     */
    public FlowElement getSourceRef() {
//		if(!(super.getSourceRef() instanceof ConversationElement))
//			return null;
        return super.getSourceRef();
    }

    public void setSourceRef(ConversationElement sourceEle) {
        if (sourceEle instanceof FlowElement) {
            super.setSourceRef((FlowElement) sourceEle);
        }
    }

}
