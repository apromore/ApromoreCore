/*
 * Copyright © 2009-2015 The Apromore Initiative.
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
import java.io.FileInputStream;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.apromore.anf.AnnotationsType;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.pnml.PnmlType;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import static org.junit.Assert.assertTrue;

@Ignore
public class TestPNML2CanonicalUnitTest {

    public TestPNML2CanonicalUnitTest() {}

    @Test
    public void testSomething() {
        assertTrue(true);
    }

//	/**
//	 * @param args
//	 */
//	public void main(String[] args) {
//		File foldersave = new File("PNML_models/woped_cases_mapped_cpf_anf");
//		File folder = new File("PNML_models/woped_cases_original_pnml");
//		FileFilter fileFilter = new FileFilter() {
//			public boolean accept(File file) {
//				return file.isFile();
//			}
//		};
//		File[] folderContent = folder.listFiles (fileFilter);
//		int n =0;
//		for (int i=0;i<folderContent.length;i++) {
//			File file = folderContent[i];
//			String filename = file.getName();
//			StringTokenizer tokenizer = new StringTokenizer(filename, ".");
//			String filename_without_path = tokenizer.nextToken();
//			String extension = filename.split("\\.")[filename.split("\\.").length-1];
//			if (extension.compareTo("pnml")==0) {
//				System.out.println ("Analysing " + filename);
//				n++;
//				try{
//
//
//					JAXBContext jc = JAXBContext.newInstance("org.apromore.pnml");
//					Unmarshaller u = jc.createUnmarshaller();
//					XMLReader reader = XMLReaderFactory.createXMLReader();
//
//					//Create the filter (to add namespace) and set the xmlReader as its parent.
//					NamespaceFilter inFilter = new NamespaceFilter("pnml.woped.org", true);
//					inFilter.setParent(reader);
//
//					//Prepare the input, in this case a java.io.File (output)
//					InputSource is = new InputSource(new FileInputStream(file));
//
//					//Create a SAXSource specifying the filter
//					SAXSource source = new SAXSource(inFilter, is);
//					JAXBElement<PnmlType> rootElement = (JAXBElement<PnmlType>) u.unmarshal(source);
//					PnmlType pnml = rootElement.getValue();
//
//
//
//
//					PNML2Canonical pn = new PNML2Canonical(pnml, filename_without_path );
//
//					jc = JAXBContext.newInstance("org.apromore.cpf");
//					Marshaller m = jc.createMarshaller();
//					m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
//					JAXBElement<CanonicalProcessType> cprocRootElem =
//						new org.apromore.cpf.ObjectFactory().createCanonicalProcess(pn.getCPF());
//					m.marshal(cprocRootElem, new File(foldersave,filename_without_path + ".cpf"));
//
//					jc = JAXBContext.newInstance("org.apromore.anf");
//					m = jc.createMarshaller();
//					m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
//					JAXBElement<AnnotationsType> annsRootElem = new org.apromore.anf.ObjectFactory().createAnnotations(pn.getANF());
//					m.marshal(annsRootElem, new File (foldersave,filename_without_path + ".anf"));
//
//
//
//				} catch (JAXBException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			} else {
//				System.out.println ("Skipping " + filename);
//			}
//		}
//		System.out.println ("Analysed " + n + " files.");
//	}

}
