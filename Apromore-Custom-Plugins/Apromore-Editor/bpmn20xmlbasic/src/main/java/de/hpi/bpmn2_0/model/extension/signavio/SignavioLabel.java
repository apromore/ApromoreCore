
package de.hpi.bpmn2_0.model.extension.signavio;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 The University of Melbourne.
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * #L%
 */

import de.hpi.bpmn2_0.model.extension.AbstractExtensionElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores label's positions information.
 *
 * @author Sven Wagner-Boysen
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SignavioLabel extends AbstractExtensionElement {

    @XmlAnyAttribute
    private Map<QName, String> labelAttributes;

    /*
      * Constructors
      */
    public SignavioLabel() {
        super();
    }

    public SignavioLabel(Map<String, String> labelInfo) {
        for (String key : labelInfo.keySet()) {
            getLabelAttributes().put(new QName(key), labelInfo.get(key));
        }
    }

    /* Getter & Setter */

    public Map<QName, String> getLabelAttributes() {
        if (labelAttributes == null) {
            labelAttributes = new HashMap<QName, String>();
        }
        return labelAttributes;
    }

}
