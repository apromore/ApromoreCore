/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.deckfour.xes;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 * 
 */

public class SchemaTest {

	private void runHttpTest() {
		try {
        	URI uriXES = URI.create("http://www.xes-standard.org/xes22.xsd");
            BufferedInputStream isXES = new BufferedInputStream(uriXES.toURL().openStream());
            Source xesSchemaFile = new StreamSource(isXES);

            URI uriEXT = URI.create("http://www.xes-standard.org/xesext22.xsd");
            BufferedInputStream isEXT = new BufferedInputStream(uriEXT.toURL().openStream());
            Source extSchemaFile = new StreamSource(isEXT);             

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);              

            Schema schema = schemaFactory.newSchema(new Source[] {xesSchemaFile, extSchemaFile});
               
		    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		    parserFactory.setNamespaceAware(true);
		    parserFactory.setSchema(schema);

		    SAXParser parser = parserFactory.newSAXParser();

		    SimpleHandler handler = new SimpleHandler();
			
			JFileChooser fileChooser = new JFileChooser();
	    	int choice = fileChooser.showDialog(null, "Select XES File");
	    	if (choice == JFileChooser.APPROVE_OPTION) {    		
	    		String absolutePath = fileChooser.getSelectedFile().getAbsolutePath();
	    		parser.parse(new File(absolutePath), handler);
	    	}	
	    	
			
        } catch (SAXException e) {
               e.printStackTrace();
        } catch (MalformedURLException e) {
               e.printStackTrace();
        } catch (IOException e) {
               e.printStackTrace();               
        } catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}
	
	private void runFileTest() {
		try {
            Source xesSchemaFile = new StreamSource(new File("documentation/xes22.xsd"));

            Source extSchemaFile = new StreamSource(new File("documentation/xesext22.xsd"));

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);              

            Schema schema = schemaFactory.newSchema(new Source[] {xesSchemaFile, extSchemaFile});
               
		    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		    parserFactory.setNamespaceAware(true);
		    parserFactory.setSchema(schema);

		    SAXParser parser = parserFactory.newSAXParser();

		    SimpleHandler handler = new SimpleHandler();
			
			JFileChooser fileChooser = new JFileChooser();
	    	int choice = fileChooser.showDialog(null, "Select XES File");
	    	if (choice == JFileChooser.APPROVE_OPTION) {    		
	    		String absolutePath = fileChooser.getSelectedFile().getAbsolutePath();
	    		parser.parse(new File(absolutePath), handler);
	    	}	
	    	
			
        } catch (SAXException e) {
               e.printStackTrace();
        } catch (MalformedURLException e) {
               e.printStackTrace();
        } catch (IOException e) {
               e.printStackTrace();               
        } catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}
	
	private static class SimpleHandler extends DefaultHandler {
		
	      public void warning(SAXParseException e) throws SAXException {
	         System.out.println("Warning: "); 
	         printInfo(e);
	      }
	      public void error(SAXParseException e) throws SAXException {
	         System.out.println("Error: "); 
	         printInfo(e);
	      }
	      public void fatalError(SAXParseException e) throws SAXException {
	         System.out.println("Fattal error: "); 
	         printInfo(e);
	      }
	      private void printInfo(SAXParseException e) {
	         System.out.println("   Public ID: "+e.getPublicId());
	         System.out.println("   System ID: "+e.getSystemId());
	         System.out.println("   Line number: "+e.getLineNumber());
	         System.out.println("   Column number: "+e.getColumnNumber());
	         System.out.println("   Message: "+e.getMessage());
	      }
	   }	
	    
    public static void main(String[] args) {
    	Locale.setDefault(Locale.UK);
    	SchemaTest validator = new SchemaTest();
    	validator.runFileTest();
    }

}
