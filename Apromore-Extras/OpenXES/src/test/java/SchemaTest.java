import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
