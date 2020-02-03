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

package de.hpi.bpmn2_0.model.gateway;

import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 * This class summarizes {@link Gateway} that associate a default
 * {@link SequenceFlow}.
 *
 * @author Sven Wagner-Boysen
 */
public abstract class GatewayWithDefaultFlow extends Gateway {

    @XmlAttribute(name = "default")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected SequenceFlow defaultSequenceFlow;

    /**
     * Determines the default {@link SequenceFlow}
     *
     * @return The default {@link SequenceFlow} or null
     */
    public SequenceFlow findDefaultSequenceFlow() {
        for (SequenceFlow seqFlow : this.getOutgoingSequenceFlows()) {
            /* A default sequence flow should not have an condition expression. */
            if (seqFlow.isDefaultSequenceFlow()) {
                this.setDefault(seqFlow);
                return seqFlow;
            }
        }

        return null;
    }

    public void acceptVisitor(Visitor v) {
        v.visitGatewayWithDefaultFlow(this);
    }

    /* Getter & Setter */

    /**
     * Gets the default {@link SequenceFlow} of the {@link ExclusiveGateway} or
     * null if no default flow is set.
     *
     * @return possible object is
     *         {@link SequenceFlow }
     */
    public SequenceFlow getDefault() {
        return defaultSequenceFlow;
    }

    /**
     * Sets default {@link SequenceFlow}.
     *
     * @param value allowed object is
     *              {@link SequenceFlow }
     */
    public void setDefault(SequenceFlow value) {
        this.defaultSequenceFlow = value;
    }
}
