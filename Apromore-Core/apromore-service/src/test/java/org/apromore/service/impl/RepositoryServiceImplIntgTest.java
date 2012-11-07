package org.apromore.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;

import org.apromore.TestData;
import org.apromore.graph.canonical.Canonical;
import org.apromore.model.ProcessSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.ProcessService;
import org.apromore.service.RepositoryService;
import org.apromore.service.model.CanonisedProcess;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Unit test the CanoniserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class RepositoryServiceImplIntgTest {

    @Autowired
    private CanoniserService cSrv;
    @Autowired
    private ProcessService pSrv;
    @Autowired
    private RepositoryService rSrv;

    private HashSet<RequestParameterType<?>> emptyCanoniserRequest;

    @Before
    public void setUp() {
        emptyCanoniserRequest = new HashSet<RequestParameterType<?>>();
    }

    @Test
    @Rollback(true)
    public void testGetProcessModel() throws Exception {
        // Firstly, Create a process so we can test.
        createProcessModel("TEST1", "99.9");

        // Now try and retrieve the process using the repo manager
        Canonical graph = rSrv.getProcessModel("TEST1", "MAIN", "MAIN");

        // Make sure we have the correct Process.
        assertThat(graph, notNullValue());
    }

    @Test
    @Rollback(true)
    public void testGetCurrentProcessModelByProcessAndBranch() throws Exception {
        // Firstly, Create a process so we can test.
        createProcessModel("TEST2", "1.0");

        // Now try and retrieve the process using the repo manager
        Canonical graph = rSrv.getCurrentProcessModel("TEST2", "MAIN", false);

        // Make sure we have the correct Process.
        assertThat(graph, notNullValue());
    }

    @Test
    @Rollback(true)
    public void testGetCurrentProcessModelByProcess() throws Exception {
        // Firstly, Create a process so we can test.
        createProcessModel("TEST3", "1.0");

        // Now try and retrieve the process using the repo manager
        Canonical graph = rSrv.getCurrentProcessModel("TEST3", false);

        // Make sure we have the correct Process.
        assertThat(graph, notNullValue());
    }


    private void createProcessModel(final String name, final String version) throws Exception {
        String nativeType = "EPML 2.0";

        InputStream data = new ByteArrayInputStream(TestData.EPML5.getBytes());
        CanonisedProcess cp = cSrv.canonise(nativeType, data, emptyCanoniserRequest);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());

        String username = "james";
        String cpfURI = "12325335343353";
        String domain = "TEST";
        String created = "01/01/2011";
        String lastUpdate = "01/01/2011";

        ProcessSummaryType pst = pSrv.importProcess(username, name, cpfURI, version, nativeType, cp, data, domain, "", created, lastUpdate);

        assertThat(pst, notNullValue());
        assertThat(pst.getDomain(), equalTo(domain));
        assertThat(pst.getOriginalNativeType(), equalTo(nativeType));
        assertThat(pst.getOwner(), equalTo(username));
    }


}
