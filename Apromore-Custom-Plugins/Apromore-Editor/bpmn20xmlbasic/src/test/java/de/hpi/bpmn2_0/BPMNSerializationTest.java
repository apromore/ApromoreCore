
package de.hpi.bpmn2_0;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.json.JSONException;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
//import org.oryxeditor.server.diagram.JSONBuilder;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;

public class BPMNSerializationTest {

	//final static String path = "C:\\Users\\Sven Wagner-Boysen\\workspace\\oryx\\editor\\server\\src\\de\\hpi\\bpmn2_0\\";
	final static String path = "platform extensions/bpmn20xmlbasic/src/de/hpi/bpmn2_0/";
	//final static String schemaFilePath = "C:\\Users\\Sven Wagner-Boysen\\workspace\\oryx\\editor\\lib\\xsd\\bpmn20\\Bpmn20.xsd";
	final static String schemaFilePath = "src/test/xsd/BPMN20.xsd";
	
	final static String batchPath = "C:\\Users\\Sven Wagner-Boysen\\Documents\\oryx\\BPMN2.0\\TestProcesses";
	final static boolean doBatchTest = false;
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		if (args.length != 1) {
			System.err.println("Must pass a single argument, the absolute path of a JSON file to convert to BPMN");
			System.exit(-1);
		}

		toBpmn2_0(args[0]);
		// fromXml();

	}
	
	public static void toBpmn2_0(String fileName) throws Exception {
		if(!doBatchTest) {
			//File json = new File(path + "trivial_bpmn2.0_process.json");
			File json = new File(fileName);
			toBpmn2_0(json);
		} else {
			File dir = new File(batchPath);
			for(File model : Arrays.asList(dir.listFiles())) {
				System.out.println("Transform File: " + model.getName() + "\n \n");
				toBpmn2_0(model);
				System.out.println("------------ \n \n");
			}
		}
	}

	public static void toBpmn2_0(File json) throws Exception {
		
		BufferedReader br = new BufferedReader(new FileReader(json));
		String bpmnJson = "";
		String line;
		while ((line = br.readLine()) != null) {
			bpmnJson += line;
		}
		BasicDiagram diagram = BasicDiagramBuilder.parseJson(bpmnJson);
		
		List<Class<? extends AbstractBpmnFactory>> factoryClasses = 
		/*
			getClassesByPackageName(AbstractBpmnFactory.class,
				"de.hpi.bpmn2_0.factory");
		*/
			AbstractBpmnFactory.getFactoryClasses();
		Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, factoryClasses);
		Definitions def = converter.getDefinitionsFromDiagram();

		// def.getOtherAttributes().put(new QName("xmlns:local"), "nurlokal");

		// final XMLStreamWriter xmlStreamWriter = XMLOutputFactory
		// .newInstance().createXMLStreamWriter(System.out);
		//		
		// xmlStreamWriter.setPrefix("bpmndi","http://bpmndi.org");

		JAXBContext context = JAXBContext.newInstance(Definitions.class,
		                                              ConfigurationAnnotationAssociation.class,
		                                              ConfigurationAnnotationShape.class);
		Marshaller m = context.createMarshaller();

		/* Schema validation */
		SchemaFactory sf = SchemaFactory
				.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new File(schemaFilePath));
		m.setSchema(schema);
		
		ExportValidationEventCollector vec = new ExportValidationEventCollector();
		m.setEventHandler(vec);
		

		// m.setListener(new ListenerTest());
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		NamespacePrefixMapper nsp = new BPMNPrefixMapper();
		m.setProperty("com.sun.xml.bind.namespacePrefixMapper", nsp);
//		StringWriter sw = new StringWriter();
//		m.marshal(def, sw);
		m.marshal(def, System.out);
		ValidationEvent[] events = vec.getEvents();
		
		StringBuilder builder = new StringBuilder();
		builder.append("Validation Errors: \n\n");
		
		for(ValidationEvent event : Arrays.asList(events)) {
			
//			builder.append("Line: ");
//			builder.append(event.getLocator().getLineNumber());
//			builder.append(" Column: ");
//			builder.append(event.getLocator().getColumnNumber());
			
			builder.append("\nError: ");
			builder.append(event.getMessage());
			builder.append("\n\n\n");
		}
		
		System.err.println(builder.toString());

		// SyntaxChecker checker = def.getSyntaxChecker();
		// checker.checkSyntax();
		//		
		// System.out.println(checker.getErrorsAsJson().toString());
	}
	
	/** Returns all classes of the specified package, who are subclasses of the given super class
	 * 
	 * @param <T>  superclass of results
	 * @param pckgname  package to search in
	 * @return subclasses of the given super class in the given package
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private static <T> List<Class<? extends T>> getClassesByPackageName(Class<T> superclass, String pckgname) throws ClassNotFoundException {
		// This will hold a list of directories matching the packagename. There may be more than one if a package is split over multiple jars/paths
      	String path = pckgname.replace('.', '/');
      	
      	
        
      	File directory = new File("bin/" + path);
      	directory.getAbsolutePath();

        ArrayList<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
        // For every directory identified capture all the .class files
        if (directory != null && directory.exists()) {
        	// Get the list of the files contained in the package
        	File[] files = directory.listFiles();
        	for (File file : files) {
        		// we are only interested in .class files
        		if (file.getName().endsWith(".class")) {
        			// removes the .class extension
        			// Get the class object
        			Class<? extends Object> cls = Class.forName(pckgname + '.' + file.getName().substring(0, file.getName().length() - 6));
        			// Checks if its an AbstractHandler
        			if( superclass.isAssignableFrom(cls) ){
        				classes.add( (Class<? extends T>) cls );
        			}
        		} else if( file.isDirectory() ) { 
        			// Add recursive all child packages
        			List<Class<? extends T>> childPackages = getClassesByPackageName(superclass, pckgname + '.' + file.getName());
        			classes.addAll( childPackages );
        		}
        	}
        } else {
        	throw new ClassNotFoundException(pckgname + " (" + directory.getPath() + ") does not appear to be a valid package");
        }
        return classes;
    }
	

	public static void fromXml() throws JAXBException, JSONException {
		//File xml = new File(path + "bpmntest.xml");
		File xml = new File("/Users/raboczi/Project/repo/Travel.bpmn20.xml");
		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Definitions def = (Definitions) unmarshaller.unmarshal(xml);

		BPMN2DiagramConverter converter = new BPMN2DiagramConverter("/oryx/");
		List<BasicDiagram> dia = converter.getDiagramFromBpmn20(def);

		//System.out.println(JSONBuilder.parseModeltoString(dia.get(0)));
		System.out.println(dia.get(0).toString());
	}
}
