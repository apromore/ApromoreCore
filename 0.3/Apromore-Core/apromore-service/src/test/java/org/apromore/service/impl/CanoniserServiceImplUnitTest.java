package org.apromore.service.impl;

import org.apromore.TestData;
import org.apromore.dao.jpa.CanonicalDaoJpa;
import org.apromore.exception.CanoniserException;
import org.apromore.service.impl.models.CanonicalNoAnnotationModel;
import org.apromore.service.impl.models.CanonicalWithAnnotationModel;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

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
@PrepareForTest({ CanonicalDaoJpa.class })
public class CanoniserServiceImplUnitTest {

    private CanoniserServiceImpl service;

    @Before
    public final void setUp() throws Exception {
        service = new CanoniserServiceImpl();
    }

    @Test
    public void deCanoniseWithoutAnnotationsFailure() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "Canonical";
        DataSource cpf = new ByteArrayDataSource("<XML/>", "text/xml");

        DataSource data = service.deCanonise(processId, version, name, cpf, null);

        MatcherAssert.assertThat(data, Matchers.nullValue());
    }

    @Test
    public void deCanoniseWithIncorrectType() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "Canonical";
        DataSource cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml");

        DataSource data = service.deCanonise(processId, version, name, cpf, null);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    @Test
    public void deCanoniseWithoutAnnotationsSuccessXPDL() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "XPDL 2.1";
        DataSource cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml");

        DataSource data = service.deCanonise(processId, version, name, cpf, null);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    @Test
    public void deCanoniseWithAnnotationsSuccessXPDL() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "XPDL 2.1";
        DataSource cpf = new ByteArrayDataSource(CanonicalWithAnnotationModel.CANONICAL_XML, "text/xml");
        DataSource anf = new ByteArrayDataSource(CanonicalWithAnnotationModel.ANNOTATION_XML, "text/xml");

        DataSource data = service.deCanonise(processId, version, name, cpf, anf);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }


    /*
     * doesn't work, shouldn't be null.
     */
    @Test
    public void deCanoniseWithoutAnnotationsSuccessEPML() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "EPML 2.0";
        DataSource cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml");

        DataSource data = service.deCanonise(processId, version, name, cpf, null);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    /*
     * doesn't work, shouldn't be null.
     */
    @Test
    public void deCanoniseWithAnnotationsSuccessEPML() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "EPML 2.0";
        DataSource cpf = new ByteArrayDataSource(CanonicalWithAnnotationModel.CANONICAL_XML, "text/xml");
        DataSource anf = new ByteArrayDataSource(CanonicalWithAnnotationModel.ANNOTATION_XML, "text/xml");

        DataSource data = service.deCanonise(processId, version, name, cpf, anf);

        assertThat(data, notNullValue());
    }


    @Test(expected = CanoniserException.class)
    public void canoniseFailureTypeNotFound() throws Exception {
        String uri = "1234567890";
        String nativeType = "PPPDL";

        InputStream data = new ByteArrayInputStream(TestData.XPDL.getBytes());

        service.canonise(uri, data, nativeType, null, null);
    }

    @Test
    public void canoniseXPDL() throws Exception {
        String uri = "1234567890";
        String nativeType = "XPDL 2.1";

        ByteArrayOutputStream anf_xml = new ByteArrayOutputStream();
        ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();

        InputStream data = new ByteArrayInputStream(TestData.XPDL.getBytes());

        service.canonise(uri, data, nativeType, anf_xml, cpf_xml);

        assertThat(anf_xml, notNullValue());
        assertThat(cpf_xml, notNullValue());
    }

    @Test
    public void canoniseEPML() throws Exception {
        String uri = "1234567890";
        String nativeType = "EPML 2.0";

        ByteArrayOutputStream anf_xml = new ByteArrayOutputStream();
        ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();

        InputStream data = new ByteArrayInputStream(TestData.EPML.getBytes());

        service.canonise(uri, data, nativeType, anf_xml, cpf_xml);

        assertThat(anf_xml, notNullValue());
        assertThat(cpf_xml, notNullValue());
    }

    @Test
    public void canoniseEPML2() throws Exception {
        String uri = "1234567890";
        String nativeType = "EPML 2.0";

        ByteArrayOutputStream anf_xml = new ByteArrayOutputStream();
        ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();

        InputStream data = new ByteArrayInputStream(TestData.EPML2.getBytes());

        service.canonise(uri, data, nativeType, anf_xml, cpf_xml);

        assertThat(anf_xml, notNullValue());
        assertThat(cpf_xml, notNullValue());
    }

    @Test
    public void canonisePNML() throws Exception {
        String uri = "1234567890";
        String nativeType = "PNML 1.3.2";

        ByteArrayOutputStream anf_xml = new ByteArrayOutputStream();
        ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();

        InputStream data = new ByteArrayInputStream(TestData.PNML.getBytes());

        service.canonise(uri, data, nativeType, anf_xml, cpf_xml);

        assertThat(anf_xml, notNullValue());
        assertThat(cpf_xml, notNullValue());
    }
}
