/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2014 Pasquale Napoli.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apromore.anf.AnnotationsType;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;

/**
 * Helps with debugging and seeing the data travel between services.
 */
public class StreamUtil {

    private static final String ANF_URI = "org.apromore.anf";
    private static final String CPF_URI = "org.apromore.cpf";
    private static final String XPDL_URI = "org.wfmc._2009.xpdl2";

    /**
     * Convert a InputStream to a String
     *
     * @param is the inputStream to convert
     * @return the string for that input stream
     */
    public static String convertStreamToString(final InputStream is) {
        return inputStream2String(is);
    }

    /**
     * Convert a DataHandler to a String
     *
     * @param dh the DataHandler to convert
     * @return the string for that DataHandler
     */
    public static String convertStreamToString(final DataHandler dh) {
        try {
            return inputStream2String(dh.getInputStream());
        } catch (IOException e) {
            return "error in readin the DataHandler: " + e.toString();
        }
    }

    /**
     * Convert a DataHandler to a String
     *
     * @param ds the DataSource to convert
     * @return the string for that DataSource
     */
    public static String convertStreamToString(final DataSource ds) {
        try {
            return inputStream2String(ds.getInputStream());
        } catch (IOException e) {
            return "error in readin the DataSource: " + e.toString();
        }
    }


    /**
     * Return an inputstream which is the result of writing parameters in anf_xml.
     *
     * @return The modified input stream.
     * @throws javax.xml.bind.JAXBException if it fails
     */
    @SuppressWarnings("unchecked")
    public static InputStream copyParam2ANF(final InputStream anf_xml, final String name) throws JAXBException {
        InputStream res;

        JAXBContext jc = JAXBContext.newInstance(ANF_URI, org.apromore.anf.ObjectFactory.class.getClassLoader());
        Unmarshaller u = jc.createUnmarshaller();

        JAXBElement<AnnotationsType> rootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf_xml);
        AnnotationsType annotations = rootElement.getValue();
        annotations.setName(name);

        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream xml = new ByteArrayOutputStream();
        m.marshal(rootElement, xml);
        res = new ByteArrayInputStream(xml.toByteArray());

        return res;
    }

    /**
     * Return an input stream which is cpf_xml where attributes are set to parameter values
     *
     * @param cpf_xml      the cpf xml
     * @param cpf_uri      the cpf id for the DB
     * @param processName  the process name
     * @param version      the process version
     * @param username     the user doing the change
     * @param creationDate the date created
     * @param lastUpdate   the updated date
     * @return The modified input stream.
     * @throws javax.xml.bind.JAXBException if it fails
     */
    @SuppressWarnings("unchecked")
    public static InputStream copyParam2CPF(final InputStream cpf_xml, final Integer cpf_uri, final String processName, final String version, final String username,
            final String creationDate, final String lastUpdate) throws JAXBException {
        InputStream res;

        JAXBContext jc = JAXBContext.newInstance(CPF_URI, org.apromore.cpf.ObjectFactory.class.getClassLoader());
        Unmarshaller u = jc.createUnmarshaller();

        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf_xml);
        CanonicalProcessType cpf = rootElement.getValue();
        cpf.setAuthor(username);
        cpf.setName(processName);
        cpf.setVersion(version);
        cpf.setCreationDate(creationDate);
        cpf.setModificationDate(lastUpdate);
        cpf.setUri(String.valueOf(cpf_uri));

        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream xml = new ByteArrayOutputStream();
        m.marshal(rootElement, xml);
        res = new ByteArrayInputStream(xml.toByteArray());
        return res;
    }

    /**
    * Converts an input stream to a string.
    *
    * @param is the input stream
    * @return the String that was the input stream
    */
    public static String inputStream2String(final InputStream is) {
        if (is != null) {
            try {
                return IOUtils.toString(is, "UTF-8");
            } catch (IOException e) {
                return "error in reading the input streams: " + e.toString();
            }
        }
        return "";
    }

}
