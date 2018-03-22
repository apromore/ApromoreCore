package au.ltl.extendedReader;

import org.processmining.plugins.declare.visualizing.Base;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.*;

/**
 * Created by armascer on 17/11/2017.
 */
public class XMLElementFactory2 {
        private static final String XML_ID = "id";
        private static final String ATTRIBUTES = "attributes";
        private final XMLBroker2 broker;

        public XMLElementFactory2(XMLBroker2 broker) {
            this.broker = broker;
        }

        public XMLElementFactory2(XMLElementFactory2 factory) {
            this(factory.broker);
        }

        public Element baseToElement(Base aBase) {
            Element id = this.getDocument().createElement("id");
            id.appendChild(this.getDocument().createTextNode(aBase.getIdString()));
            return id;
        }

        public Element baseToElement(Base aBase, String name) {
            Element id = this.getDocument().createElement(name);
            this.setAttribute(id, "id", aBase.getIdString());
            return id;
        }

        public Base elementToBase(Element element) {
            String string = element.getAttribute("id");
            int id = 0;

            try {
                id = Integer.decode(string).intValue();
            } catch (Exception var5) {
                ;
            }

            Base base = new Base(id);
            return base;
        }

        public void attributesToElement(HashMap<String, String> attributes, Element element) {
            if(element != null && attributes != null && !attributes.isEmpty()) {
                Element main = this.getDocument().createElement("attributes");
                Iterator entries = attributes.entrySet().iterator();

                while(entries.hasNext()) {
                    Map.Entry entry = (Map.Entry)entries.next();
                    Element item = this.getDocument().createElement((String)entry.getKey());
                    Text value = this.broker.createTextNode((String)entry.getValue());
                    item.appendChild(value);
                    main.appendChild(item);
                }

                element.appendChild(main);
            }

        }

        public void elementToAttributes(Element element, HashMap<String, String> attributes) {
            if(element != null && attributes != null) {
                Element main = this.findFirstElement(element, "attributes");
                if(main != null) {
                    NodeList items = main.getChildNodes();

                    for(int i = 0; i < items.getLength(); ++i) {
                        Element item = (Element)items.item(i);
                        String name = item.getNodeName();
                        String value = "";
                        Node text = item.getFirstChild();
                        if(text instanceof Text) {
                            value = text.getNodeValue();
                        }

                        attributes.put(name, value);
                    }
                }
            }

        }

        protected void setAttribute(Element element, String name, String value) {
            element.setAttribute(name, value);
        }

        protected Element createObjectAttribute(String name, String value) {
            Element nameTag = this.broker.createElement(name);
            Text valueText = this.broker.createTextNode(value);
            nameTag.appendChild(valueText);
            return nameTag;
        }

        protected void updateObjectAttribute(Element elementObject, String attrName, String attrValue) {
            Element attribute = this.getFirstElement(elementObject, attrName);
            Text value;
            if(attribute != null) {
                value = (Text)attribute.getFirstChild();
                if(value != null) {
                    value.setNodeValue(attrValue);
                } else {
                    value = this.broker.createTextNode(attrValue);
                    attribute.appendChild(value);
                }
            } else {
                attribute = this.broker.createElement(attrName);
                value = this.broker.createTextNode(attrValue);
                attribute.appendChild(value);
                elementObject.appendChild(attribute);
            }

        }

        protected void deleteElement(Element elementObject, Element elementList) {
            elementList.removeChild(elementObject);
        }

        protected Document getDocument() {
            return this.broker.getDocument();
        }

        public String getXMLid() {
            return "id";
        }

        public void removeChildren(Element element) {
            while(element.hasChildNodes()) {
                element.removeChild(element.getFirstChild());
            }

        }

        public Element getFirstElement(Element element, String name) {
            NodeList nl = element.getChildNodes();
            boolean found = false;
            Object node = null;

            for(int i = 0; i < nl.getLength() && !found; found = ((Node)node).getNodeName().equals(name)) {
                node = nl.item(i++);
            }

            if(!found) {
                node = this.broker.createElement(name);
                element.appendChild((Node)node);
            }

            return (Element)node;
        }

        public Element findFirstElement(Element element, String name) {
            NodeList nl = element.getChildNodes();
            boolean found = false;
            Node node = null;

            for(int i = 0; i < nl.getLength() && !found; found = node.getNodeName().equals(name)) {
                node = nl.item(i++);
            }

            return found?(Element)node:null;
        }

        protected List<Element> getAllSubElements(Element element, String name) {
            NodeList nl = element.getChildNodes();
            ArrayList list = new ArrayList();
            if(element.getNodeName().equals(name)) {
                list.add(element);
            }

            Node node = null;

            for(int i = 0; i < nl.getLength(); ++i) {
                node = nl.item(i);
                if(node instanceof Element) {
                    list.addAll(this.getAllSubElements((Element)node, name));
                }
            }

            return list;
        }

        protected String getSimpleElementText(Element element, String name) {
            Element nameEl = this.getFirstElement(element, name);
            Node textNode = nameEl.getFirstChild();
            return textNode instanceof Text?textNode.getNodeValue():"";
        }

        protected String getSimpleElementText(Element element) {
            Node textNode = element.getFirstChild();
            return textNode instanceof Text?textNode.getNodeValue():"";
        }

        public Element createElement(String name) {
            return this.broker.createElement(name);
        }

        protected Text createTextNode(String text) {
            return this.broker.createTextNode(text);
        }
    }
