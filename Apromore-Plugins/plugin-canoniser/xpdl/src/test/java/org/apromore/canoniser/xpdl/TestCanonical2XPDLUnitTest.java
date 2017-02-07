/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.canoniser.xpdl;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.xpdl.internal.Canonical2XPDL;
import org.apromore.cpf.CanonicalProcessType;
import org.junit.Ignore;
import org.junit.Test;
import org.wfmc._2009.xpdl2.PackageType;

import static org.junit.Assert.assertTrue;

@Ignore
public class TestCanonical2XPDLUnitTest {

    public TestCanonical2XPDLUnitTest() {}

    @Test
    public void testSomething() {
        assertTrue(true);
    }

	/**
	 * @param args
	 * @throws ExceptionAdapters 
	 */
	public void main(String[] args)  {
		File cpf_file = new File("/powderfinger/home/fauvet/models/eeeee.cpf");
		File anf_initial_file = new File("/powderfinger/home/fauvet/models/test/test-blue-initial.anf");
		File anf_green_file = new File("/powderfinger/home/fauvet/models/test/test-blue-green.anf");
		//File cpf_file = new File("/home/fauvet/models/model1.cpf");
		//File anf_file = new File("/home/fauvet/models/model1.anf");
		try {
			JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf_file);
			CanonicalProcessType cpf = rootElement.getValue();

//			jc = JAXBContext.newInstance("org.apromore.anf");
//			u = jc.createUnmarshaller();
//			JAXBElement<AnnotationsType> anfRootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf_initial_file);
//			AnnotationsType anf = anfRootElement.getValue();
			
//			Canonical2XPDL canonical2xpdl_with_anf = new Canonical2XPDL (cpf, anf);
			Canonical2XPDL canonical2xpdl_no_anf = new Canonical2XPDL(cpf);
			
			jc = JAXBContext.newInstance("org.wfmc._2009.xpdl2");
			Marshaller m1 = jc.createMarshaller();
			m1.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			
			JAXBElement<PackageType> cprocRootElem1 = 
				new org.wfmc._2009.xpdl2.ObjectFactory().createPackage(canonical2xpdl_no_anf.getXpdl());
			m1.marshal(cprocRootElem1, new File("/coldplay/home/fauvet/models/eeeee.xpdl"));
			
//			jc = JAXBContext.newInstance("org.apromore.anf");
//			u = jc.createUnmarshaller();
//			anfRootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf_green_file);
//			anf = anfRootElement.getValue();
			
//			canonical2xpdl_with_anf = new Canonical2XPDL (cpf, anf);
//			jc = JAXBContext.newInstance("org.wfmc._2009.xpdl2");
//			Marshaller m2 = jc.createMarshaller();
//			m2.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
//			
//			JAXBElement<PackageType> cprocRootElem2 = 
//				new org.wfmc._2009.xpdl2.ObjectFactory().createPackage(canonical2xpdl_with_anf.getXpdl());
//			m1.marshal(cprocRootElem2, new File("/coldplay/home/fauvet/models/test/test-blue-green.xpdl"));
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
