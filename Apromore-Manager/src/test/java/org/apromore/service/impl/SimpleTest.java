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

package org.apromore.service.impl;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.epml.EPML20Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.converter.CanonicalToGraph;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginRequestImpl;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.FragmentNode;
import org.apromore.util.MutableTreeConstructor;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.utils.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Simple test to test some functions.
 */
public class SimpleTest {

    @Test
    public void testImportProcessWithSubProcessesInYAWL() throws Exception {
        PluginRequest plugin = new PluginRequestImpl();
        List<AnnotationsType> anfList = new ArrayList<>();
        List<CanonicalProcessType> cpfList = new ArrayList<>();
        EPML20Canoniser epmlCanoniser = new EPML20Canoniser();

        CanonisedProcess cp = new CanonisedProcess();

        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/1Be_3era.epml"), "text/xml"));
        epmlCanoniser.canonise(stream.getInputStream(), anfList, cpfList, plugin);

        ByteArrayOutputStream anfXml = new ByteArrayOutputStream();
        ByteArrayOutputStream cpfXml = new ByteArrayOutputStream();

        if (cpfList.size() > 1 || anfList.size() > 1) {
            throw new CanoniserException("Canonising to multiple CPF, ANF files is not yet supported!");
        } else {
            try {
                ANFSchema.marshalAnnotationFormat(anfXml, anfList.get(0), false);
                cp.setAnf(new ByteArrayInputStream(anfXml.toByteArray()));
                cp.setAnt(anfList.get(0));

                CPFSchema.marshalCanonicalFormat(cpfXml, cpfList.get(0), false);
                cp.setCpf(new ByteArrayInputStream(cpfXml.toByteArray()));
                cp.setCpt(cpfList.get(0));

            } catch (JAXBException | SAXException e) {
                throw new CanoniserException("Error trying to marshal ANF or CPF. This is probably an internal error in a Canoniser.", e);
            }
        }

        Canonical graph = new CanonicalToGraph().convert(cp.getCpt());
        RPST<CPFEdge, CPFNode> rpst = new RPST(graph);
        FragmentNode rf = new MutableTreeConstructor().construct(rpst);
        IOUtils.toFile("output.dot", graph.toDOT());
        IOUtils.toFile("outputRpst.dot", rpst.toDOT());
        IOUtils.toFile("outputRF.dot", rf.toDOT());
        IOUtils.invokeDOT("target/", "output.png", graph.toDOT());
        IOUtils.invokeDOT("target/", "outputRpst.png", rpst.toDOT());
        IOUtils.invokeDOT("target/", "outputRF.png", rf.toDOT());

    }
}
