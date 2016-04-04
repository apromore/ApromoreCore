/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.signavio.warehouse.model.syntaxchecker.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author gero.decker
 */
public class StencilSetUtil {

    public String getStencilSet(Document doc) {
        Node node = doc.getDocumentElement();
        if (node == null || !node.getNodeName().equals("rdf:RDF"))
            return null;

        node = node.getFirstChild();
        while (node != null) {
            Node typeChild = getChild(node, "rdf:type");
            if (typeChild != null) {
                String resource = getAttributeValue(typeChild, "rdf:resource");
                if (resource != null && resource.equals("http://oryx-editor.org/canvas")) break;
            }
            node = node.getNextSibling();
        }
        if (node != null) {
            String type = getAttributeValue(getChild(node, "stencilset"), "rdf:resource");
            if (type != null) {
                type = type.substring(type.lastIndexOf('/') + 1);
                int versionIndex = type.indexOf("?version=");
                if (versionIndex > 0) {
                    type = type.substring(0, versionIndex);
                }
                return type;
            }
        }
        return null;
    }

//	protected String getContent(Node node) {
//		if (node != null && node.hasChildNodes())
//			return node.getFirstChild().getNodeValue();
//		return null;
//	}

    private String getAttributeValue(Node node, String attribute) {
        if (node.getAttributes() != null) { // text nodes have no attributes
            Node item = node.getAttributes().getNamedItem(attribute);
            if (item != null)
                return item.getNodeValue();
        }

        return null;
    }

    private Node getChild(Node n, String name) {
        if (n == null)
            return null;
        for (Node node = n.getFirstChild(); node != null; node = node.getNextSibling())
            if (node.getNodeName().indexOf(name) >= 0)
                return node;
        return null;
    }

}


