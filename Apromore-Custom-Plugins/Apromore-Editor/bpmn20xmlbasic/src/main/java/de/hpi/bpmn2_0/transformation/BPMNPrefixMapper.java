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

package de.hpi.bpmn2_0.transformation;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import java.util.HashMap;
import java.util.Map;


/**
 * The namespace prefix mapper is responsible for the creation of user friendly
 * namespace prefixes in the BPMN 2.0 XML document.
 *
 * @author Sven Wagner-Boysen
 */
public class BPMNPrefixMapper extends NamespacePrefixMapper {

    private Map<String, String> nsDefs;

    private static Map<String, String> customExtensions = new HashMap<String, String>();

    /* (non-Javadoc)
      * @see com.sun.xml.bind.marshaller.NamespacePrefixMapper#getPreferredPrefix(java.lang.String, java.lang.String, boolean)
      */
    // @Override
    public String getPreferredPrefix(String namespace, String suggestion, boolean isRequired) {

        /* BPMN 2.0 Standard Namespaces */
        if (namespace.equals("http://www.omg.org/spec/BPMN/20100524/MODEL"))
            return "";
        else if (namespace.equals("http://www.omg.org/spec/BPMN/20100524/DI"))
            return "bpmndi";
        else if (namespace.equals("http://www.w3.org/2001/XMLSchema-instance"))
            return "xsi";
        else if (namespace.equals("http://www.omg.org/spec/DD/20100524/DI"))
            return "omgdi";
        else if (namespace.equals("http://www.omg.org/spec/DD/20100524/DC"))
            return "omgdc";

            /* Signavio */
        else if (namespace.equals("http://www.signavio.com"))
            return "signavio";

            /* Check custom extension */
        else if (getCustomExtensions().get(namespace) != null) {
            return getCustomExtensions().get(namespace);
        }

        /* Check namespace definitions from external XML elements */
        return getNsDefs().get(namespace);

    }

    public String[] getPreDeclaredNamespaceUris() {
        super.getPreDeclaredNamespaceUris();
        String[] s = {};
        return this.getNsDefs().keySet().toArray(s);
    }

    public static Map<String, String> getCustomExtensions() {

        Constants c = Diagram2BpmnConverter.getConstants();
        if (c == null) {
            return new HashMap<String, String>();
        }

        return new HashMap<String, String>(c.getCustomNamespacePrefixMappings());
    }

    /* Getter & Setter */

    public Map<String, String> getNsDefs() {
        if (nsDefs == null) {
            nsDefs = new HashMap<String, String>();
        }

        return nsDefs;
    }

    public void setNsDefs(Map<String, String> nsDefs) {
        this.nsDefs = nsDefs;
    }

}
