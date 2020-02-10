

package de.hpi.bpmn2_0.model.activity;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
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
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

import de.hpi.bpmn2_0.annotations.CallingElement;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.CallableElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.bpmndi.BPMNDiagram;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tCallActivity complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tCallActivity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tActivity">
 *       &lt;attribute name="calledElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCallActivity")
public class CallActivity
        extends Activity implements CallingElement {
    /* Constructors */
    public CallActivity() {
        super();
    }

    public CallActivity(Task t) {
        super(t);
    }

    @XmlTransient
    private List<FlowElement> processElements;

    /*
      * The diagram and process element of a linked subprocess
      */
    @XmlTransient
    public BPMNDiagram _diagramElement;

    @XmlAttribute
    @XmlIDREF
    protected CallableElement calledElement;

    /**
     * Gets the value of the calledElement property.
     *
     * @return possible object is
     *         {@link CallableElement }
     */
    public CallableElement getCalledElement() {
        return calledElement;
    }

    /**
     * Sets the value of the calledElement property.
     *
     * @param value allowed object is
     *              {@link CallableElement }
     */
    public void setCalledElement(CallableElement value) {
        this.calledElement = value;
    }

    public void acceptVisitor(Visitor v) {
        v.visitCallActivity(this);
    }

    public List<BaseElement> getCalledElements() {
        List<BaseElement> calledElements = new ArrayList<BaseElement>();

        if (getCalledElement() != null) {
            calledElements.add(getCalledElement());
        }

        return calledElements;
    }

    /**
     * Overrides the general addChild method to collect elements of a call
     * activity expanded sub process.
     */
    public void addChild(BaseElement el) {
        if (el instanceof FlowElement) {
            this._getFlowElementsOfTheGlobalProcess().add((FlowElement) el);
        }
    }

    /* Getter & Setter */

    /**
     * !!! Only for usages during the BPMN 2.0 Export process.
     * Returns the elements the expanded subprocess called be this call activity.
     */
    public List<FlowElement> _getFlowElementsOfTheGlobalProcess() {
        if (this.processElements == null) {
            this.processElements = new ArrayList<FlowElement>();
        }

        return this.processElements;
    }
}
