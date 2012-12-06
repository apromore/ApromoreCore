package org.apromore.service.impl;

import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.converter.CanonicalToGraph;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.ProcessService;
import org.apromore.service.model.CanonisedProcess;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.utils.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit test the UserService Implementation.
 *
 * TODO: Improve the Test Asserts.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ImportProcessServiceImplIntgTest {

    @Inject
    private CanoniserService cSrv;
    @Inject
    private ProcessService pSrv;

    private String username = "james";
    private String version = "1.0";
    private String domain = "Tests";
    private String created = "12/12/2011";
    private String lastUpdate = "12/12/2011";


    @Test
    @Rollback(true)
    public void testImportProcessWithSingleEdgeInEPML() throws Exception {
        String natType = "EPML 2.0";
        String name = "Test EPML 1";
        String cpfURI = "1";

        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/test1.epml"), "text/xml"));
        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>());
        ProcessModelVersion pst = pSrv.importProcess(username, name, cpfURI, version, natType, cp, stream.getInputStream(), domain, "", created, lastUpdate);

        assertThat(pst, notNullValue());
    }

    @Test
    @Rollback(true)
    public void testImportProcessWithJoinAndSplitInEPML() throws Exception {
        String natType = "EPML 2.0";
        String name = "Test EPML 2";
        String cpfURI = "2";

        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/test2.epml"), "text/xml"));
        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>());
        ProcessModelVersion pst = pSrv.importProcess(username, name, cpfURI, version, natType, cp, stream.getInputStream(), domain, "", created, lastUpdate);

        assertThat(pst, notNullValue());
    }

    @Test
    @Rollback(true)
    public void testImportProcessWithObjectsResourcesInXPDL() throws Exception {
        //String natType = "XPDL 2.1";
        String natType = "EPML 2.0";
        String name = "Test XPDL 1";
        String cpfURI = "3";

        //DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("XPDL_models/F3 International Departure Passport Control.xpdl"), "text/xml"));
        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/SAP_1_2.epml"), "text/xml"));
        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>());
        ProcessModelVersion pst = pSrv.importProcess(username, name, cpfURI, version, natType, cp, stream.getInputStream(), domain, "", created, lastUpdate);

        assertThat(pst, notNullValue());

//        Canonical graph = new CanonicalToGraph().convert(cp.getCpt());
//        RPST<CPFEdge, CPFNode> rpst = new RPST(graph);
//        IOUtils.toFile("graph.dot", graph.toDOT());
//        IOUtils.invokeDOT("target/", "graph.png", graph.toDOT());
    }

    @Test
    @Rollback(true)
    public void testImportProcessWithSubProcessesInYAWL() throws Exception {
        String natType = "YAWL 2.2";
        String name = "Test YAWL 1";
        String cpfURI = "4";

        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("YAWL_models/orderfulfillment.yawl"), "text/xml"));
        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>());
        ProcessModelVersion pst = pSrv.importProcess(username, name, cpfURI, version, natType, cp, stream.getInputStream(), domain, "", created, lastUpdate);

        // TODO: Make sure you do the Sub Processes.
    }



//    @Test
//    @Rollback(true)
//    public void testImportSimpleYAWLProcess() throws CanoniserException, IOException, ImportException {
//        String natType = "YAWL 2.2";
//
//        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("YAWL_models/WPC2ParallelSplit.yawl"), "text/xml"));
//        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>());
//        ProcessSummaryType pst = pSrv.importProcess(username, name, cpfURI, version, natType, cp, stream.getInputStream(), domain, "", created, lastUpdate);
//
//        assertThat(pst, notNullValue());
//        assertThat(pst.getDomain(), equalTo(domain));
//        assertThat(pst.getOriginalNativeType(), equalTo(natType));
//        assertThat(pst.getOwner(), equalTo(username));
//    }
//
//    @Test
//    @Rollback(true)
//    public void testImportComplexYAWLProcess() throws CanoniserException, IOException, ImportException {
//        String natType = "YAWL 2.2";
//
//        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("YAWL_models/PaymentSubnet.yawl"), "text/xml"));
//        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>());
//        ProcessSummaryType pst = pSrv.importProcess(username, name, cpfURI, version, natType, cp, stream.getInputStream(), domain, "", created, lastUpdate);
//
//        assertThat(pst, notNullValue());
//        assertThat(pst.getDomain(), equalTo(domain));
//        assertThat(pst.getOriginalNativeType(), equalTo(natType));
//        assertThat(pst.getOwner(), equalTo(username));
//    }
//
//    @Test
//    @Rollback(true)
//    public void testImportComplexYAWLProcessWithSubnets() throws CanoniserException, IOException, ImportException, JAXBException {
//        String natType = "YAWL 2.2";
//
//        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("YAWL_models/orderfulfillment.yawl"), "text/xml"));
//        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>());
//        ProcessSummaryType pst = pSrv.importProcess(username, name, cpfURI, version, natType, cp, stream.getInputStream(), domain, "", created, lastUpdate);
//
//        assertThat(pst, notNullValue());
//        assertThat(pst.getDomain(), equalTo(domain));
//        assertThat(pst.getOriginalNativeType(), equalTo(natType));
//        assertThat(pst.getOwner(), equalTo(username));
//    }

}
