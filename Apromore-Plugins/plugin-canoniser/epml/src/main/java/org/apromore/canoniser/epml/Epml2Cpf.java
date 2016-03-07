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

package org.apromore.canoniser.epml;

// Java 2 Standard packages
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;

// Local packages
import de.epml.CorrectedEPML;
import de.epml.EPMLSchema;
import de.epml.TypeEPML;
import org.apromore.canoniser.epml.internal.EPML2Canonical;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CPFSchema;

/**
 * Command line tool for canonizing EPML files.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class Epml2Cpf {

    /**
     * Application entry point.
     *
     * @param arg  command line arguments; either empty or a single argument, the filename of the ANF
     */
    public static void main(String[] arg) throws CanoniserException, IOException, JAXBException, SAXException, TransformerException {

        // Parse EPML from the standard input stream
        ByteArrayInputStream in = new ByteArrayInputStream(new CorrectedEPML(new StreamSource(System.in)).toByteArray());
        TypeEPML epml = EPMLSchema.unmarshalEPMLFormat(in, true /* validation enabled */).getValue();

        // Canonize EPML to CPF
        CanonicalProcessType cpf = new EPML2Canonical(epml).getCPF();

        // Serialize the CPF to the standard output stream
        CPFSchema.marshalCanonicalFormat(System.out, cpf, true /* validation enabled */);
    }
}
