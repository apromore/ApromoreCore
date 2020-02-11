
package de.hpi.bpmn2_0.model.extension;

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

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Java class for tExtensionElements complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tExtensionElements">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tExtensionElements"/*, propOrder = { "any" }*/)
public class ExtensionElements {

    //	@XmlAnyElement(lax = true)
    @XmlElementRef(type = AbstractExtensionElement.class)
    protected List<AbstractExtensionElement> any;

    @XmlAnyElement
    protected List<Element> anyExternal;

    /**
     * Returns the first element of type {@code elementType}.
     *
     * @param elementType
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractExtensionElement> T getFirstExtensionElementOfType(
            Class<T> elementType) {
        for (AbstractExtensionElement element : getAny()) {
            if (element.getClass().equals(elementType)) {
                return (T) element;
            }
        }

        return null;
    }

    /**
     * Returns the first element of type {@code elementType}.
     *
     * @param elementType
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractExtensionElement> List<T> getAllElementOfType(
            Class<T> elementType) {
        List<T> elements = new ArrayList<T>();
        for (AbstractExtensionElement element : getAny()) {
            if (element.getClass().equals(elementType)) {
                elements.add((T) element);
            }
        }

        return elements;
    }

    /**
     * Add an {@link AbstractExtensionElement} to the extension elements list.
     *
     * @param extensionElement
     */
    public void add(AbstractExtensionElement extensionElement) {
        getAny().add(extensionElement);
    }

    /**
     * Gets the value of the any property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the any property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getAny().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list {@link Element }
     * {@link AbstractExtensionElement }
     */
    public List<AbstractExtensionElement> getAny() {
        if (any == null) {
            any = new ArrayList<AbstractExtensionElement>();
        }
        return this.any;
    }

    public List<Element> getAnyExternal() {
        if (anyExternal == null) {
            anyExternal = new ArrayList<Element>();
        }

        return anyExternal;
    }


}
