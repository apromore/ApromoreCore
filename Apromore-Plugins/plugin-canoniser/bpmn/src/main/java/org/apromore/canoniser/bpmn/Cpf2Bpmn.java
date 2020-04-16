/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.bpmn.anf.AnfAnnotationsType;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.message.PluginMessage;

/**
 * Command line tool for de-canonizing BPMN files.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class Cpf2Bpmn {

    /**
     * Application entry point.
     *
     * @param arg  command line arguments, currently ignored
     */
    public static void main(String[] arg) throws CanoniserException, IOException, JAXBException, SAXException {
        CanonicalProcessType cpf = CpfCanonicalProcessType.newInstance(new FileInputStream(arg[0]), true);
        AnnotationsType      anf = null;

        // If there's a command line argument, treat it as the filename of an ANF file
        if (arg.length > 1) {
            anf = AnfAnnotationsType.newInstance(new FileInputStream(arg[1]), true);
        }

        // Read BPMN from the input stream
        PluginResult result = new BPMN20Canoniser().deCanonise(cpf, anf, System.out, null);

        // Write warnings to the error stream
        for (PluginMessage message : result.getPluginMessage()) {
            System.err.println(message.getMessage());
        }
    }
}
