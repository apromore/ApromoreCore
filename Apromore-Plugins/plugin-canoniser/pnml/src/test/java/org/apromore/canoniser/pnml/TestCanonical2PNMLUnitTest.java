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

package org.apromore.canoniser.pnml;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.cpf.CanonicalProcessType;

import org.apromore.anf.AnnotationsType;
import org.apromore.pnml.ObjectFactory;
import org.apromore.pnml.PnmlType;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

@Ignore
public class TestCanonical2PNMLUnitTest {

	File anf_file = null;
	File cpf_file = null;
	File foldersave = new File("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml");
	File output = null;

    public TestCanonical2PNMLUnitTest() {}

    @Test
    public void testSomething() {
        assertTrue(true);
    }

//	/**
//	 * @param args
//	 */
//	public void main(String[] args) {
//		String cpf_file_without_path = null;
//		String anf_file_without_path = null;
//
//		File anf_file = null;
//		File cpf_file = null;
//		File foldersave = new File("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml");
//		File output = null;
//		File folder = new File("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf");
//		FileFilter fileFilter = new FileFilter() {
//			public boolean accept(File file) {
//				return file.isFile();
//			}
//		};
//		File[] folderContent = folder.listFiles(fileFilter);
//		int n = 0;
//
//        for (File file : folderContent) {
//            String filename = file.getName();
//            StringTokenizer tokenizer = new StringTokenizer(filename, ".");
//            String filename_without_path = tokenizer.nextToken();
//
//            String extension = filename.split("\\.")[filename.split("\\.").length - 1];
//
//            output = new File(foldersave + "/" + filename_without_path + ".pnml");
//
//            if (!filename.contains("subnet")) {
//                if (extension.compareTo("cpf") == 0
//                        && extension.compareTo("anf") == 0) {
//                    System.out.println("Skipping " + filename);
//                }
//
//                if (extension.compareTo("anf") == 0) {
//                    System.out.println("Analysing " + filename);
//                    n++;
//                    anf_file = new File(folder + "/" + filename);
//                    anf_file_without_path = filename_without_path;
//                }
//
//                if (extension.compareTo("cpf") == 0) {
//                    System.out.println("Analysing " + filename);
//                    n++;
//                    cpf_file = new File(folder + "/" + filename);
//                    cpf_file_without_path = filename_without_path;
//
//                }
//            }
//
//            if (anf_file != null && cpf_file != null
//                    && anf_file_without_path != null
//                    && cpf_file_without_path != null
//                    && anf_file_without_path.equals(cpf_file_without_path)
//                    && !filename.contains("subnet")) {
//
//                try {
//                    JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
//                    Unmarshaller u = jc.createUnmarshaller();
//                    JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf_file);
//                    CanonicalProcessType cpf = rootElement.getValue();
//
//                    jc = JAXBContext.newInstance("org.apromore.anf");
//                    u = jc.createUnmarshaller();
//                    JAXBElement<AnnotationsType> anfRootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf_file);
//                    AnnotationsType anf = anfRootElement.getValue();
//
//                    jc = JAXBContext.newInstance("org.apromore.pnml");
//
//                    Canonical2PNML canonical2pnml = new Canonical2PNML(cpf,
//                            anf, filename_without_path);
//
//                    Marshaller m1 = jc.createMarshaller();
//
//                    NamespaceFilter outFilter = new NamespaceFilter(null, false);
//
//                    OutputFormat format = new OutputFormat();
//                    format.setIndent(true);
//                    format.setNewlines(true);
//                    format.setXHTML(true);
//                    format.setExpandEmptyElements(true);
//                    format.setNewLineAfterDeclaration(false);
//
//                    XMLWriter writer = null;
//                    try {
//                        writer = new XMLWriter(new FileOutputStream(output), format);
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                    // Attach the writer to the filter
//                    outFilter.setContentHandler(writer);
//
//                    // Tell JAXB to marshall to the filter which in turn will
//                    // call the writer
//                    m1.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//                    JAXBElement<PnmlType> cprocRootElem1 = new ObjectFactory().createPnml(canonical2pnml.getPNML());
//                    m1.marshal(cprocRootElem1, outFilter);
//
//                } catch (JAXBException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//		System.out.println("Analysed " + n + " files.");
//
//	}

}
