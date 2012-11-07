package org.apromore.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.util.HashSet;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.TestData;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.exception.ImportException;
import org.apromore.model.ProcessSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.ProcessService;
import org.apromore.service.model.CanonisedProcess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class ProcessServiceImplIntgTest {

    @Autowired
    private CanoniserService cSrv;

    @Autowired
    private ProcessService pSrv;

    @Test
    @Rollback(true)
    public void TestStandardImportProcess() throws Exception {
        String username = "james";
        String name = "Test Version Control";
        String cpfURI = "12325335343353";
        String version = "1.0";
        String natType = "XPDL 2.1";
        String domain = "Airport";
        String created = "12/12/2011";
        String lastUpdate = "12/12/2011";
        DataHandler stream = new DataHandler(new ByteArrayDataSource(TestData.XPDL2.getBytes(), "text/xml"));

        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>());

        ProcessSummaryType pst = pSrv.importProcess(username, name, cpfURI, version, natType, cp, stream.getInputStream(), domain, "", created, lastUpdate);

        assertThat(pst, notNullValue());
        assertThat(pst.getDomain(), equalTo(domain));
        assertThat(pst.getOriginalNativeType(), equalTo(natType));
        assertThat(pst.getOwner(), equalTo(username));
    }


    @Test
    @Rollback(true)
    public void testImportSimpleYAWLProcess() throws CanoniserException, IOException, ImportException {
        String username = "Test";
        String name = "Test Version Control";
        String cpfURI = "12325335343353";
        String version = "1.0";
        String natType = "YAWL 2.2";
        String domain = "Airport";
        String created = "12/12/2011";
        String lastUpdate = "12/12/2011";
        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("YAWL_models/WPC2ParallelSplit.yawl"), "text/xml"));

        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>());

        ProcessSummaryType pst = pSrv.importProcess(username, name, cpfURI, version, natType, cp, stream.getInputStream(), domain, "", created, lastUpdate);

        assertThat(pst, notNullValue());
        assertThat(pst.getDomain(), equalTo(domain));
        assertThat(pst.getOriginalNativeType(), equalTo(natType));
        assertThat(pst.getOwner(), equalTo(username));
    }

    @Test
    @Rollback(true)
    public void testImportComplexYAWLProcess() throws CanoniserException, IOException, ImportException {
        String username = "Test";
        String name = "Test Version Control";
        String cpfURI = "12325335343353";
        String version = "1.0";
        String natType = "YAWL 2.2";
        String domain = "Airport";
        String created = "12/12/2011";
        String lastUpdate = "12/12/2011";
        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("YAWL_models/PaymentSubnet.yawl"), "text/xml"));

        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>());

        ProcessSummaryType pst = pSrv.importProcess(username, name, cpfURI, version, natType, cp, stream.getInputStream(), domain, "", created, lastUpdate);

        assertThat(pst, notNullValue());
        assertThat(pst.getDomain(), equalTo(domain));
        assertThat(pst.getOriginalNativeType(), equalTo(natType));
        assertThat(pst.getOwner(), equalTo(username));
    }

    @Test
    @Rollback(true)
    public void testImportComplexYAWLProcessWithSubnets() throws CanoniserException, IOException, ImportException {
        String username = "Test";
        String name = "Test Version Control";
        String cpfURI = "12325335343353";
        String version = "1.0";
        String natType = "YAWL 2.2";
        String domain = "Airport";
        String created = "12/12/2011";
        String lastUpdate = "12/12/2011";
        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("YAWL_models/oderfulfillment.yawl"), "text/xml"));

        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>());

        ProcessSummaryType pst = pSrv.importProcess(username, name, cpfURI, version, natType, cp, stream.getInputStream(), domain, "", created, lastUpdate);

        assertThat(pst, notNullValue());
        assertThat(pst.getDomain(), equalTo(domain));
        assertThat(pst.getOriginalNativeType(), equalTo(natType));
        assertThat(pst.getOwner(), equalTo(username));
    }

}
