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

package org.apromore.service.impl;

// Java 2 Standard packages
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

// Third party packages
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

// Local packages
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CPFSchema;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.model.Canonical;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.helper.Version;
import org.apromore.model.ParametersType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessVersionType;
import org.apromore.model.ProcessVersionsType;
import org.apromore.service.CanoniserService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.toolbox.similaritySearch.algorithms.FindModelSimilarity;
import org.apromore.toolbox.similaritySearch.common.CPFModelParser;
import org.apromore.toolbox.similaritySearch.common.IdGeneratorHelper;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.tools.SearchForSimilarProcesses;

/**
 * Test suite for {@link SimilarityServiceImpl}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class SimilarityServiceImplUnitTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private CanoniserService canSvc;
    private FolderRepository fldDao;
    private ProcessModelVersionRepository pmvDao;
    private SimilarityServiceImpl service;
    private UserInterfaceHelper uiHelp;

    @Before
    public final void setUp() throws Exception {

        pmvDao = createMock(ProcessModelVersionRepository.class);
        fldDao = createMock(FolderRepository.class);
        canSvc = createMock(CanoniserService.class);
        uiHelp = createMock(UserInterfaceHelper.class);

        service = new SimilarityServiceImpl(pmvDao, fldDao, canSvc, uiHelp);
    }

    /**
     * Test {@link SearchForSimilarProcesses}
     */
    @Test
    public void testSearchForSimilarProcesses() throws Exception {
        final Integer processId = 123;
        final String name = "One Two Three";
        final String version = "1.2";
        final Version versionNumber = new Version(1,0);

        NativeType natType = new NativeType();
        natType.setNatType("PNML 1.3.2");

        org.apromore.dao.model.Process process = new org.apromore.dao.model.Process();
        process.setId(processId);
        process.setNativeType(natType);

        ProcessBranch branch = new ProcessBranch();
        branch.setId(processId);
        branch.setBranchName(name);
        branch.setProcess(process);
       
        ProcessModelVersion pmv = new ProcessModelVersion();
        pmv.setId(processId);
        pmv.setNativeType(natType);
        pmv.setProcessBranch(branch);

        Canonical canonical = new Canonical();
        pmv.setCanonicalDocument(canonical);

        List<ProcessModelVersion> pmvList = new ArrayList<>();
        pmvList.add(pmv);

        CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(new FileInputStream("src/test/resources/CPF_models/test1.cpf"), true).getValue();

        ProcessVersionType pv = new ProcessVersionType();
        pv.setProcessId(processId);
        pv.setVersionName(name);
        pv.setScore(1.0);  // <-- this is the result under test

        ProcessVersionsType pvs = new ProcessVersionsType();
        pvs.getProcessVersion().add(pv);

        expect(pmvDao.getLatestProcessModelVersion(processId, "MAIN")).andReturn(pmv);
        expect(pmvDao.getLatestProcessModelVersionsByUser("admin")).andReturn(pmvList);
        expect(canSvc.XMLtoCPF(null)).andReturn(cpf).times(2);
        expect(uiHelp.buildProcessSummaryList("admin", 0, pvs)).andReturn(null);  // <-- this is where we compare expected and actual results

        replay(pmvDao, fldDao, canSvc, uiHelp);

        ParametersType parameters = new ParametersType();
        ProcessSummariesType ps = service.SearchForSimilarProcesses(
		processId,   // process id
		"MAIN",      // branch name
		true,        // only latest versions?
		0,           // folder id
		"admin",     // user id
		"Hungarian", // method
		parameters);

        verify(pmvDao, fldDao, canSvc, uiHelp);
    }

    /**
     * Test {@link SearchForSimilarProcesses#findProcessesSimilarity}
     */
    @Test
    public void testFindProcessesSimilarity() throws Exception {

        CanonicalProcessType cpf1 = CPFSchema.unmarshalCanonicalFormat(new FileInputStream("src/test/resources/CPF_models/test1.cpf"), true).getValue();
        CanonicalProcessType cpf2 = CPFSchema.unmarshalCanonicalFormat(new FileInputStream("src/test/resources/CPF_models/test1.cpf"), true).getValue();

        double similarity = SearchForSimilarProcesses.findProcessesSimilarity(cpf1, cpf2, "Hungarian", 0.75, 0.5);
        assertEquals(1.0, similarity, 0.001);
    }

    /**
     * Test {@link FindModelSimilarity#findProcessSimilarity}
     */
    @Test
    public void testFindModelSimilarity() throws Exception {

        Graph a = readGraph("src/test/resources/CPF_models/test1.cpf");
        Graph b = readGraph("src/test/resources/CPF_models/test2.cpf");
        
        assertEquals(1.0, FindModelSimilarity.findProcessSimilarity(a, a, "Greedy", 0.5, 0.5, 0.5, 0.5, 0.5), 0.001);
        assertEquals(1.0, FindModelSimilarity.findProcessSimilarity(a, a, "Hungarian", 0.5, 0.5), 0.001);
        assertEquals(0.6, FindModelSimilarity.findProcessSimilarity(a, b, "Greedy", 0.5, 0.5, 0.5, 0.5, 0.5), 0.001);
        assertEquals(0.667, FindModelSimilarity.findProcessSimilarity(a, b, "Hungarian", 0.5, 0.5), 0.001);
    }

    private Graph readGraph(String fileName) throws Exception {
        CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(new FileInputStream(fileName), true).getValue();
        Graph graph = CPFModelParser.readModel(cpf);
        graph.setIdGenerator(new IdGeneratorHelper());
        graph.removeEmptyNodes();
        return graph;
    }
}
