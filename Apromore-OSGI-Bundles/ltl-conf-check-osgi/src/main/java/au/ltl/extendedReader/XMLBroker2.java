package au.ltl.extendedReader;

import org.processmining.plugins.declare.visualizing.Broker;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * Created by armascer on 17/11/2017.
 */
public class XMLBroker2 extends Broker {
        private Document document;
        private InputStream file;
        private final String name;
        private String xml = null;

        public XMLBroker2(InputStream file, String aName) {
            super("", false);
            this.file = file;

            this.name = aName;
            this.connect();
        }

        protected Document getDocument() {
            return this.document;
        }

        private boolean createDocument() {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;

            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException var4) {
                return false;
            }

            DOMImplementation domImpl = builder.getDOMImplementation();
            this.document = domImpl.createDocument((String)null, this.name, (DocumentType)null);
            return true;
        }

        public Element createElement(String name) {
            return this.document.createElement(name);
        }

        public Text createTextNode(String text) {
            return this.document.createTextNode(text);
        }

        protected void connect() {
            this.createDocument();
        }

        public boolean writeDocument() {
//            TransformerFactory tf = TransformerFactory.newInstance();
//            Transformer transformer = null;
//
//            try {
//                transformer = tf.newTransformer();
//            } catch (TransformerConfigurationException var8) {
//                return false;
//            }
//
//            DOMSource source = new DOMSource(this.document);
//            ByteArrayOutputStream stream = null;
//            StreamResult output;
//            if(this.file == null) {
//                output = new StreamResult(stream = new ByteArrayOutputStream());
//                transformer.setOutputProperty("omit-xml-declaration", "yes");
//            } else {
////                output = new StreamResult(this.file);
//            }
//
//            try {
//                transformer.transform(source, output);
//            } catch (TransformerException var7) {
//                return false;
//            }
//
//            if(stream != null) {
//                this.xml = stream.toString();
//            }

            return true;
        }

        public boolean readDocument() {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = null;

            try {
                parser = factory.newDocumentBuilder();
            } catch (ParserConfigurationException var6) {
                return false;
            }

            try {
                this.document = parser.parse(file);
                return true;
            } catch (IOException var4) {
                return false;
            } catch (SAXException var5) {
                return false;
            }
        }

        public boolean readDocument(String path) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = null;

            try {
                parser = factory.newDocumentBuilder();
            } catch (ParserConfigurationException var7) {
                return false;
            }

            try {
                this.document = parser.parse(new InputSource(path));
                return true;
            } catch (IOException var5) {
                return false;
            } catch (SAXException var6) {
                return false;
            }
        }

        public boolean readDocumentString(String documentString) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = null;

            try {
                parser = factory.newDocumentBuilder();
            } catch (ParserConfigurationException var8) {
                return false;
            }

            try {
                byte[] ex1 = documentString.getBytes();
                ByteArrayInputStream stringIS = new ByteArrayInputStream(ex1);
                this.document = parser.parse(stringIS);
                return true;
            } catch (IOException var6) {
                return false;
            } catch (SAXException var7) {
                return false;
            }
        }

        protected Element getDocumentRoot() {
            return this.getDocument().getDocumentElement();
        }

        protected void clearDocument() {
            this.getDocument().removeChild(this.getDocumentRoot());
        }

        protected void deleteElement(Element elementObject, Element elementList) {
            elementList.removeChild(elementObject);
        }

        public String getXML() {
            return this.xml;
        }
    }