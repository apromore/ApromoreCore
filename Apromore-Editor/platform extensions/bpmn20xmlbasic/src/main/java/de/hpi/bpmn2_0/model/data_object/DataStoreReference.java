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

package de.hpi.bpmn2_0.model.data_object;

import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;

/**
 * A DataStoreReference provides a reference to a globally defined
 * {@link DataObject}.
 *
 * @author Sven Wagner-Boysen
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDataStoreReference")
public class DataStoreReference extends AbstractDataObject {

    @XmlAttribute
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected DataStore dataStoreRef;

    /**
     * Helper for the import, see {@link FlowElement#isElementWithFixedSize().
     */
    // @Override
    public boolean isElementWithFixedSize() {
        return true;
    }

    /**
     * For the fixed-size shape, return the fixed width.
     */
    public double getStandardWidth() {
        return 63.001;
    }

    /**
     * For the fixed-size shape, return the fixed height.
     */
    public double getStandardHeight() {
        return 61.173;
    }

    public void acceptVisitor(Visitor v) {
        v.visitDataStoreReference(this);
    }

    public void setProcess(Process process) {
        super.setProcess(process);
        if (this.dataStoreRef != null)
            this.dataStoreRef.setProcessRef(process);

    }

    /* Getter & Setter */

    /**
     * Gets the value of the dataStoreRef property.
     *
     * @return possible object is {@link DataStore }
     */
    public DataStore getDataStoreRef() {
        return dataStoreRef;
    }

    /**
     * Sets the value of the dataStoreRef property.
     *
     * @return possible object is {@link DataStore }
     */
    public void setDataStoreRef(DataStore dataStoreRef) {
        this.dataStoreRef = dataStoreRef;
    }

}
