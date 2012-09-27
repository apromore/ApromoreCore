package org.apromore.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apromore.TestData;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.provider.impl.SimpleSpringCanoniserProvider;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.graph.JBPT.CPF;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.service.impl.models.CanonicalNoAnnotationModel;
import org.apromore.service.impl.models.CanonicalWithAnnotationModel;
import org.apromore.service.model.CanonisedProcess;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jbpt.graph.algo.DirectedGraphAlgorithms;
import org.jbpt.graph.algo.rpst.RPST;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
public class CanoniserServiceImplUnitTest {

    private CanoniserServiceImpl service;

    @Before
    public final void setUp() throws Exception {
        service = new CanoniserServiceImpl();
        //TODO this should be a MOCK instead!! as it is an integration test for now
        service.setCanoniserProvider(new SimpleSpringCanoniserProvider());
    }

    @Test(expected = JAXBException.class)
    public void deCanoniseWithoutAnnotationsFailure() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "Canonical";
        InputStream cpf = new ByteArrayDataSource("<XML/>", "text/xml").getInputStream();

        service.deCanonise(processId, version, name, getTypeFromXML(cpf), null);
    }

    @Test
    public void deCanoniseWithIncorrectType() throws IOException {
        Integer processId = 123;
        String version = "1.2";
        String name = "Canonical";
        InputStream cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml").getInputStream();

        DataSource data;
        try {
            data = service.deCanonise(processId, version, name, getTypeFromXML(cpf), null);
            fail();
        } catch (CanoniserException e) {
            assertTrue(e.getCause() instanceof PluginNotFoundException);
        } catch (JAXBException e) {
            fail();
        }
    }

    @Test
    public void deCanoniseWithoutAnnotationsSuccessXPDL() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "XPDL 2.1";
        InputStream cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml").getInputStream();

        DataSource data = service.deCanonise(processId, version, name, getTypeFromXML(cpf), null);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    @Test
    public void deCanoniseWithAnnotationsSuccessXPDL() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "XPDL 2.1";
        InputStream cpf = new ByteArrayDataSource(CanonicalWithAnnotationModel.CANONICAL_XML, "text/xml").getInputStream();
        DataSource anf = new ByteArrayDataSource(CanonicalWithAnnotationModel.ANNOTATION_XML, "text/xml");

        DataSource data = service.deCanonise(processId, version, name, getTypeFromXML(cpf), anf);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }


    /*
     * doesn't work, shouldn't be null.
     */
    @Test
    public void deCanoniseWithoutAnnotationsSuccessEPML() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "EPML 2.0";
        InputStream cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml").getInputStream();

        DataSource data = service.deCanonise(processId, version, name, getTypeFromXML(cpf), null);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    /*
     * doesn't work, shouldn't be null.
     */
    @Test
    public void deCanoniseWithAnnotationsSuccessEPML() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "EPML 2.0";
        InputStream cpf = new ByteArrayDataSource(CanonicalWithAnnotationModel.CANONICAL_XML, "text/xml").getInputStream();
        DataSource anf = new ByteArrayDataSource(CanonicalWithAnnotationModel.ANNOTATION_XML, "text/xml");

        DataSource data = service.deCanonise(processId, version, name, getTypeFromXML(cpf), anf);

        assertThat(data, notNullValue());
    }


    @Test(expected = CanoniserException.class)
    public void canoniseFailureTypeNotFound() throws Exception {
        String uri = "1234567890";
        String nativeType = "PPPDL";

        InputStream data = new ByteArrayInputStream(TestData.XPDL.getBytes());

        service.canonise(nativeType, uri, data);
    }

    @Test
    public void canoniseXPDL() throws Exception {
        String uri = "1234567890";
        String nativeType = "XPDL 2.1";

        InputStream data = new ByteArrayInputStream(TestData.XPDL.getBytes());
        CanonisedProcess cp = service.canonise(nativeType, uri, data);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());
    }

    @Test
    public void canoniseEPML() throws Exception {
        String uri = "1234567890";
        String nativeType = "EPML 2.0";

        InputStream data = new ByteArrayInputStream(TestData.EPML.getBytes());
        CanonisedProcess cp = service.canonise(nativeType, uri, data);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());
    }

    @Test
    public void canoniseEPML2() throws Exception {
        String uri = "1234567890";
        String nativeType = "EPML 2.0";

        InputStream data = new ByteArrayInputStream(TestData.EPML2.getBytes());
        CanonisedProcess cp = service.canonise(nativeType, uri, data);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());
    }

    @Test
    public void canonisePNML() throws Exception {
        String uri = "1234567890";
        String nativeType = "PNML 1.3.2";

        InputStream data = new ByteArrayInputStream(TestData.PNML.getBytes());
        CanonisedProcess cp = service.canonise(nativeType, uri, data);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());
    }


    @Test
    @SuppressWarnings("unchecked")
    public void deserialize() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Unmarshaller u = jc.createUnmarshaller();
        InputStream data = new ByteArrayInputStream(TestData.CPF2.getBytes());
        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(data);
        CanonicalProcessType canType = rootElement.getValue();

        CPF graph = service.deserializeCPF(canType);
        assertThat(graph, notNullValue());

        DirectedGraphAlgorithms<ControlFlow<FlowNode>, FlowNode> dga = new DirectedGraphAlgorithms<ControlFlow<FlowNode>, FlowNode>();
        assertThat(dga.isCyclic(graph), is(false));

        RPST<ControlFlow<FlowNode>, FlowNode> rpst = new RPST<ControlFlow<FlowNode>, FlowNode>(graph);
        assertThat(rpst, notNullValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserialize2() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Unmarshaller u = jc.createUnmarshaller();
        InputStream data = new ByteArrayInputStream(TestData.CPF2.getBytes());
        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(data);
        CanonicalProcessType canType = rootElement.getValue();

        CPF graph = service.deserializeCPF(canType);
        assertThat(graph, notNullValue());

        CanonicalProcessType canTyp2 = service.serializeCPF(graph);
        assertThat(canTyp2, notNullValue());
    }

    private CanonicalProcessType getTypeFromXML(final InputStream cpf) throws JAXBException {
        CanonicalProcessType type;
        JAXBContext jc1 = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Unmarshaller u = jc1.createUnmarshaller();
        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf);
        type = rootElement.getValue();
        return type;
    }


}
