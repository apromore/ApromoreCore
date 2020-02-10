
/**
 *	The package contains special extensions of properties
 */
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

import de.hpi.bpmn2_0.transformation.Constants;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlValue;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * JAXB does not support dynamic naming of XML elements. The property list is
 * implemented like a hash map. The class name represents the name of the
 * element and content is the value of the xml element.
 *
 * @author Sven Wagner-Boysen
 */
@XmlSeeAlso(DummyPropertyListItem.class)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class PropertyListItem {

    private static List<Class<? extends PropertyListItem>> classes = new ArrayList<Class<? extends PropertyListItem>>();

    private static List<Class<? extends PropertyListItem>> getItemClasses() {
        List<Class<? extends PropertyListItem>> classesList = new ArrayList<Class<? extends PropertyListItem>>(classes);

        Constants c = Diagram2BpmnConverter.getConstants();
        if (c == null) {
            return classesList;
        }

        classesList.addAll(c.getAdditionalPropertyItemClasses());

        return classesList;
    }

    /**
     * Default constructor
     */
    public PropertyListItem() {
    }

    /**
     * Constructor to set the value of the element directly.
     *
     * @param propertyIdentifier Identifier for the appropriate class
     * @param value              The value of the property
     */
    public static PropertyListItem addItem(String propertyName, String value) {

        /* Find property class */
        Class<? extends PropertyListItem> propertyItemClass = null;
        for (Class<? extends PropertyListItem> propItemClass : getItemClasses()) {
            if (propItemClass.getSuperclass() == null
                    || !propItemClass.getSuperclass().equals(PropertyListItem.class))
                continue;

            PropertyId propId = propItemClass.getAnnotation(PropertyId.class);
            if (propId == null)
                continue;

            if (propId.value() != null && propId.value().equals(propertyName)) {
                propertyItemClass = (Class<? extends PropertyListItem>) propItemClass;
            }
        }

        /* Create instance of property item */
        if (propertyItemClass == null)
            return null;

        try {

            PropertyListItem propItem = propertyItemClass.newInstance();
            propItem.setContent(value);
            return propItem;

        } catch (Exception e) {
            return null;
        }
    }

    @XmlValue
    protected String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Target({
            ElementType.TYPE
    })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PropertyId {
        String value();
    }
}

