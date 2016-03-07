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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

// Local packages
import de.epml.EPMLSchema;
import de.epml.TypeEPML;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.ANFSchema;
import org.apromore.canoniser.epml.internal.Canonical2EPML;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CPFSchema;

/**
 * Command line tool for de-canonizing EPML files.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class Cpf2Epml {

    /**
     * Application entry point.
     *
     * @param arg  command line arguments; either empty or a single argument, the filename of the ANF
     */
    public static void main(String[] arg) throws CanoniserException, IOException, JAXBException, SAXException {

        CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(System.in, true /* validation enabled */).getValue();
        AnnotationsType      anf = null;

        // If there's a command line argument, treat it as the filename of an ANF file
        if (arg.length > 0) {
            anf = ANFSchema.unmarshalAnnotationFormat(new FileInputStream(arg[0]), true /* validation enabled */).getValue();
        }

        // Decanonize to EPML
        TypeEPML epml = new Canonical2EPML(cpf, anf).getEPML();

        // Serialize the EPML to the standard output stream
        EPMLSchema.marshalEPMLFormat(System.out, epml, true /* validation enabled */);
    }
}
