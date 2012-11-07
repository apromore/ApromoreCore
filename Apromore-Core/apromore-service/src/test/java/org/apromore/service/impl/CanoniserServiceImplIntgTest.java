package org.apromore.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apromore.TestData;
import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.ImportException;
import org.apromore.model.ProcessSummaryType;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.ProcessService;
import org.apromore.service.impl.models.CanonicalNoAnnotationModel;
import org.apromore.service.impl.models.CanonicalWithAnnotationModel;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.DecanonisedProcess;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

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
public class CanoniserServiceImplIntgTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanoniserServiceImplIntgTest.class);

    @Autowired
    private CanoniserService cSrv;

    @Autowired
    private ProcessService pSrv;

    private HashSet<RequestParameterType<?>> epmlCanoniserRequest;

    private HashSet<RequestParameterType<?>> emptyCanoniserRequest;

    @Before
    public void setUp() {
        epmlCanoniserRequest = new HashSet<RequestParameterType<?>>();
        epmlCanoniserRequest.add(new RequestParameterType<Boolean>("addFakeEvents", true));
        emptyCanoniserRequest = new HashSet<RequestParameterType<?>>();
    }

    @Rollback(true)
    @Test
    public void integrationWithProcessService() throws IOException, CanoniserException, JAXBException, SAXException, ImportException {
        String nativeType = "EPML 2.0";
        String name ="_____test";
        String version = "0.1";

        InputStream data = new ByteArrayInputStream(TestData.EPML3.getBytes());
        CanonisedProcess cp = cSrv.canonise(nativeType, data, emptyCanoniserRequest);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());

        String username = "james";
        String cpfURI = "12325335343353";
        String domain = "Airport";
        String created = "12/12/2011";
        String lastUpdate = "12/12/2011";

        ProcessSummaryType pst = pSrv.importProcess(username, name, cpfURI, version, nativeType, cp, data, domain, "", created, lastUpdate);

        assertThat(pst, notNullValue());
        assertThat(pst.getDomain(), equalTo(domain));
        assertThat(pst.getOriginalNativeType(), equalTo(nativeType));
        assertThat(pst.getOwner(), equalTo(username));
    }

    @Test(expected = JAXBException.class)
    public void deCanoniseWithoutAnnotationsFailure() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "Canonical";
        InputStream cpf = new ByteArrayDataSource("<XML/>", "text/xml").getInputStream();
        cSrv.deCanonise(processId, version, name, getTypeFromXML(cpf), null, emptyCanoniserRequest);
    }

    @Test
    public void deCanoniseWithIncorrectType() throws IOException {
        Integer processId = 123;
        String version = "1.2";
        String name = "Canonical";
        InputStream cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml").getInputStream();

        try {
            cSrv.deCanonise(processId, version, name, getTypeFromXML(cpf), null, emptyCanoniserRequest);
            fail();
        } catch (CanoniserException e) {
            assertTrue(e.getCause() instanceof PluginNotFoundException);
        } catch (JAXBException e) {
            fail();
        } catch (SAXException e) {
            fail();
        }
    }

    @Test
    public void deCanoniseWithoutAnnotationsSuccessXPDL() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "XPDL 2.1";
        InputStream cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml").getInputStream();

        DecanonisedProcess dp = cSrv.deCanonise(processId, version, name, getTypeFromXML(cpf), null, emptyCanoniserRequest);

        MatcherAssert.assertThat(dp.getNativeFormat(), Matchers.notNullValue());
        MatcherAssert.assertThat(dp.getMessages(), Matchers.notNullValue());
    }

    @Test
    public void deCanoniseWithAnnotationsSuccessXPDL() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "XPDL 2.1";
        InputStream cpf = new ByteArrayDataSource(CanonicalWithAnnotationModel.CANONICAL_XML, "text/xml").getInputStream();
        DataSource anf = new ByteArrayDataSource(CanonicalWithAnnotationModel.ANNOTATION_XML, "text/xml");

        DecanonisedProcess dp = cSrv.deCanonise(processId, version, name, getTypeFromXML(cpf), getANFTypeFromXML(anf), emptyCanoniserRequest);

        MatcherAssert.assertThat(dp.getNativeFormat(), Matchers.notNullValue());
        MatcherAssert.assertThat(dp.getMessages(), Matchers.notNullValue());
    }


    @Test
    public void deCanoniseWithoutAnnotationsSuccessEPML() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "EPML 2.0";
        InputStream cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml").getInputStream();

        Set<Canoniser> availableCanonisers = cSrv.listByNativeType(name);
        Iterator<Canoniser> iter = availableCanonisers.iterator();
        assertTrue(iter.hasNext());
        Canoniser c = iter.next();
        assertNotNull(c);
        assertEquals(name, c.getNativeType());
        assertEquals(0, c.getMandatoryParameters().size());
        assertEquals(1, c.getOptionalParameters().size());

        DecanonisedProcess dp = cSrv.deCanonise(processId, version, name, getTypeFromXML(cpf), null, epmlCanoniserRequest);

        MatcherAssert.assertThat(dp.getNativeFormat(), Matchers.notNullValue());
        MatcherAssert.assertThat(dp.getMessages(), Matchers.notNullValue());
    }


    @Test
    public void deCanoniseWithAnnotationsSuccessEPML() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "EPML 2.0";
        InputStream cpf = new ByteArrayDataSource(CanonicalWithAnnotationModel.CANONICAL_XML, "text/xml").getInputStream();
        DataSource anf = new ByteArrayDataSource(CanonicalWithAnnotationModel.ANNOTATION_XML, "text/xml");

        Set<Canoniser> availableCanonisers = cSrv.listByNativeType(name);
        Iterator<Canoniser> iter = availableCanonisers.iterator();
        assertTrue(iter.hasNext());
        Canoniser c = iter.next();
        assertNotNull(c);
        assertEquals(name, c.getNativeType());
        assertEquals(0, c.getMandatoryParameters().size());
        assertEquals(1, c.getOptionalParameters().size());

        Set<ParameterType<?>> optionalProperties = c.getOptionalParameters();
        assertEquals(1, optionalProperties.size());


        DecanonisedProcess dp = cSrv.deCanonise(processId, version, name, getTypeFromXML(cpf), getANFTypeFromXML(anf), epmlCanoniserRequest);

        MatcherAssert.assertThat(dp.getNativeFormat(), Matchers.notNullValue());
        MatcherAssert.assertThat(dp.getMessages(), Matchers.notNullValue());
    }


    @Test(expected = CanoniserException.class)
    public void canoniseFailureTypeNotFound() throws Exception {
        String nativeType = "PPPDL";
        InputStream data = new ByteArrayInputStream(TestData.XPDL.getBytes());
        cSrv.canonise(nativeType, data, emptyCanoniserRequest);
    }

    @Test
    public void canoniseXPDL() throws Exception {
        String nativeType = "XPDL 2.1";

        InputStream data = new ByteArrayInputStream(TestData.XPDL.getBytes());
        CanonisedProcess cp = cSrv.canonise(nativeType, data, emptyCanoniserRequest);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());
    }

    @Test
    public void canoniseEPML() throws Exception {
        String uri = "1234567890";
        String nativeType = "EPML 2.0";

        InputStream data = new ByteArrayInputStream(TestData.EPML.getBytes());
        CanonisedProcess cp = cSrv.canonise(nativeType, data, emptyCanoniserRequest);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());
    }

    @Test
    public void canoniseEPML2() throws Exception {
        String uri = "1234567890";
        String nativeType = "EPML 2.0";

        InputStream data = new ByteArrayInputStream(TestData.EPML2.getBytes());
        CanonisedProcess cp = cSrv.canonise(nativeType, data, emptyCanoniserRequest);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());
    }

    @Test
    public void canonisePNML() throws Exception {
        String uri = "1234567890";
        String nativeType = "PNML 1.3.2";

        InputStream data = new ByteArrayInputStream(TestData.PNML.getBytes());
        CanonisedProcess cp = cSrv.canonise(nativeType, data, emptyCanoniserRequest);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());
    }

    private CanonicalProcessType getTypeFromXML(final InputStream cpf) throws JAXBException, SAXException {
        return CPFSchema.unmarshalCanonicalFormat(cpf, false).getValue();
    }

    private AnnotationsType getANFTypeFromXML(final DataSource anf) throws JAXBException, SAXException, IOException {
        return ANFSchema.unmarshalAnnotationFormat(anf.getInputStream(), false).getValue();
    }


    @Test
    public void canoniseOrderfulfilmentFromYAWL() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canoniseOrderfulfillment();

        if (LOGGER.isDebugEnabled()) {
            // Save CPF
            saveCanonisedProcess(oFCanonised, "OrderFulfillment.cpf");
        }
    }

    @Test
    public void canoniseOrderfulfilmentFromYAWLToYAWL() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canoniseOrderfulfillment();
        DecanonisedProcess deCanonisedYAWL = cSrv.deCanonise(1, oFCanonised.getCpt().getVersion(), "YAWL 2.2", oFCanonised.getCpt(), null, new HashSet<RequestParameterType<?>>());

        if (LOGGER.isDebugEnabled()) {
            saveDecanonisedProcess(deCanonisedYAWL, "OrderFulfillment.yawl");
        }
    }

    //TODO fix and enable test (cf. http://apromore-build.qut.edu.au/jira/browse/APP-4)
    @Test
    public void convertOrderfulfilmentFromYAWLToEPML() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canoniseOrderfulfillment();
        try {
            DecanonisedProcess decanonisedEPML = cSrv.deCanonise(1, oFCanonised.getCpt().getVersion(), "EPML 2.0", oFCanonised.getCpt(), null, new HashSet<RequestParameterType<?>>());
            fail("Should throw exception because State is not supported!");
        } catch (CanoniserException e) {
        }
    }

    //TODO fix and enable test (cf. http://apromore-build.qut.edu.au/jira/browse/APP-5)
    @Ignore
    @Test
    public void convertOrderfulfilmentFromYAWLToBPMN() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canoniseOrderfulfillment();
        DecanonisedProcess decanonisedBPMN = cSrv.deCanonise(1, oFCanonised.getCpt().getVersion(), "BPMN 2.0", oFCanonised.getCpt(), null, new HashSet<RequestParameterType<?>>());
        assertNotNull(decanonisedBPMN);

        if (LOGGER.isDebugEnabled()) {
            saveDecanonisedProcess(decanonisedBPMN, "OrderFulfillment.bpmn");
        }
    }

    //TODO fix and enable test (cf. http://apromore-build.qut.edu.au/jira/browse/APP-6)
    @Ignore
    @Test
    public void convertOrderfulfilmentFromYAWLToPNML() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canoniseOrderfulfillment();
        DecanonisedProcess decanonisedPNML = cSrv.deCanonise(1, oFCanonised.getCpt().getVersion(), "PNML 1.3.2", oFCanonised.getCpt(), null, new HashSet<RequestParameterType<?>>());
        assertNotNull(decanonisedPNML);

        if (LOGGER.isDebugEnabled()) {
            saveDecanonisedProcess(decanonisedPNML, "OrderFulfillment.pnml");
        }
    }

    @Test
    public void convertOrderfulfilmentFromYAWLToXPDL() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canoniseOrderfulfillment();
        DecanonisedProcess decanonisedXPDL = cSrv.deCanonise(1, oFCanonised.getCpt().getVersion(), "XPDL 2.1", oFCanonised.getCpt(), null, new HashSet<RequestParameterType<?>>());
        assertNotNull(decanonisedXPDL);

        if (LOGGER.isDebugEnabled()) {
            saveDecanonisedProcess(decanonisedXPDL, "OrderFulfillment.xpdl");
        }
    }

    private CanonisedProcess canoniseOrderfulfillment() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised;
        try (InputStream oFProcess = ClassLoader.getSystemResourceAsStream("YAWL_models/orderfulfillment.yawl")) {
            try (InputStream oFProcessOrgData = ClassLoader.getSystemResourceAsStream("YAWL_models/orderfulfillment.ybkp")) {

                HashSet<RequestParameterType<?>> yawlParameters = new HashSet<RequestParameterType<?>>();
                yawlParameters.add(new RequestParameterType<InputStream>("readOrgData", oFProcessOrgData));
                oFCanonised = cSrv.canonise("YAWL 2.2", oFProcess, yawlParameters);

            }
        }
        return oFCanonised;
    }

    private void saveCanonisedProcess(final CanonisedProcess canonisedProcess, final String fileName) {
        try (FileOutputStream fileOutputStream = new FileOutputStream("target/"+fileName)) {
                CPFSchema.marshalCanoncialFormat(fileOutputStream, canonisedProcess.getCpt(), true);
                fileOutputStream.flush();
        } catch (JAXBException | SAXException | IOException e) {
            LOGGER.error("Could not save Canonised Process", e);
        }
    }

    private void saveDecanonisedProcess(final DecanonisedProcess decanonisedProcess, final String fileName) {
        try (FileOutputStream fileOutputStream = new FileOutputStream("target/"+fileName)) {
            IOUtils.copy(decanonisedProcess.getNativeFormat(), fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e) {
            LOGGER.error("Could not save DeCanonised Process", e);
        }
    }


    @Test
    public void convertPaymentSubnetFromYAWL() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canonisePaymentSubProcess();

        if (LOGGER.isDebugEnabled()) {
            // Save CPF
            saveCanonisedProcess(oFCanonised, "PaymentSubnet.cpf");
        }
    }

    @Test
    public void canonisePaymentSubnetFromYAWLToYAWL() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canonisePaymentSubProcess();
        DecanonisedProcess deCanonisedYAWL = cSrv.deCanonise(1, oFCanonised.getCpt().getVersion(), "YAWL 2.2", oFCanonised.getCpt(), null, new HashSet<RequestParameterType<?>>());

        if (LOGGER.isDebugEnabled()) {
            saveDecanonisedProcess(deCanonisedYAWL, "PaymentSubnet.yawl");
        }
    }


    @Test
    public void convertPaymentSubnetFromYAWLToEPML() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canonisePaymentSubProcess();

        DecanonisedProcess decanonisedEPML = cSrv.deCanonise(1, oFCanonised.getCpt().getVersion(), "EPML 2.0", oFCanonised.getCpt(), null, new HashSet<RequestParameterType<?>>());
        assertNotNull(decanonisedEPML);

        if (LOGGER.isDebugEnabled()) {
            saveDecanonisedProcess(decanonisedEPML, "PaymentSubnet.epml");
        }
    }

    @Ignore
    @Test
    public void convertPaymentSubnetFromYAWLToPNML() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canonisePaymentSubProcess();

        DecanonisedProcess decanonisedPNML = cSrv.deCanonise(1, oFCanonised.getCpt().getVersion(), "PNML 1.3.2", oFCanonised.getCpt(), null, new HashSet<RequestParameterType<?>>());
        assertNotNull(decanonisedPNML);

        if (LOGGER.isDebugEnabled()) {
            saveDecanonisedProcess(decanonisedPNML, "PaymentSubnet.pnml");
        }
    }

    @Test
    public void convertPaymentSubnetFromYAWLToXPDL() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canonisePaymentSubProcess();

        DecanonisedProcess decanonisedXPDL = cSrv.deCanonise(1, oFCanonised.getCpt().getVersion(), "XPDL 2.1", oFCanonised.getCpt(), null, new HashSet<RequestParameterType<?>>());
        assertNotNull(decanonisedXPDL);

        if (LOGGER.isDebugEnabled()) {
            saveDecanonisedProcess(decanonisedXPDL, "PaymentSubnet.xpdl");
        }
    }

    @Ignore
    @Test
    public void convertPaymentSubnetFromYAWLToBPMN() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised = canonisePaymentSubProcess();

        DecanonisedProcess decanonisedBPMN = cSrv.deCanonise(1, oFCanonised.getCpt().getVersion(), "BPMN 2.0", oFCanonised.getCpt(), null, new HashSet<RequestParameterType<?>>());
        assertNotNull(decanonisedBPMN);

        if (LOGGER.isDebugEnabled()) {
            saveDecanonisedProcess(decanonisedBPMN, "PaymentSubnet.bpmn");
        }
    }

    private CanonisedProcess canonisePaymentSubProcess() throws CanoniserException, IOException {
        CanonisedProcess oFCanonised;
        try (InputStream oFProcess = ClassLoader.getSystemResourceAsStream("YAWL_models/PaymentSubnet.yawl")) {
            try (InputStream oFProcessOrgData = ClassLoader.getSystemResourceAsStream("YAWL_models/orderfulfillment.ybkp")) {

                HashSet<RequestParameterType<?>> yawlParameters = new HashSet<RequestParameterType<?>>();
                yawlParameters.add(new RequestParameterType<InputStream>("readOrgData", oFProcessOrgData));
                oFCanonised = cSrv.canonise("YAWL 2.2", oFProcess, yawlParameters);

            }
        }
        return oFCanonised;
    }


}
