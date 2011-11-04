package org.apromore.service.impl;

import org.apromore.dao.jpa.CanonicalDaoJpa;
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

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

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
    public void DeCanoniseWithoutAnnotationsFailure() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "Canonical";
        DataSource cpf = new ByteArrayDataSource("<XML/>", "text/xml");

        DataSource data = service.DeCanonise(processId, version, name, cpf, null);

        MatcherAssert.assertThat(data, Matchers.nullValue());
    }

    @Test
    public void DeCanoniseWithIncorrectType() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "Canonical";
        DataSource cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml");

        DataSource data = service.DeCanonise(processId, version, name, cpf, null);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    @Test
    public void DeCanoniseWithoutAnnotationsSuccessXPDL() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "XPDL 2.1";
        DataSource cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml");

        DataSource data = service.DeCanonise(processId, version, name, cpf, null);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    @Test
    public void DeCanoniseWithAnnotationsSuccessXPDL() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "XPDL 2.1";
        DataSource cpf = new ByteArrayDataSource(CanonicalWithAnnotationModel.CANONICAL_XML, "text/xml");
        DataSource anf = new ByteArrayDataSource(CanonicalWithAnnotationModel.ANNOTATION_XML, "text/xml");

        DataSource data = service.DeCanonise(processId, version, name, cpf, anf);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }


    /*
     * doesn't work, shouldn't be null.
     */
    @Test
    public void DeCanoniseWithoutAnnotationsSuccessEPML() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "EPML 2.0";
        DataSource cpf = new ByteArrayDataSource(CanonicalNoAnnotationModel.CANONICAL_XML, "text/xml");

        DataSource data = service.DeCanonise(processId, version, name, cpf, null);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    /*
     * doesn't work, shouldn't be null.
     */
    @Test
    public void DeCanoniseWithAnnotationsSuccessEPML() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "EPML 2.0";
        DataSource cpf = new ByteArrayDataSource(CanonicalWithAnnotationModel.CANONICAL_XML, "text/xml");
        DataSource anf = new ByteArrayDataSource(CanonicalWithAnnotationModel.ANNOTATION_XML, "text/xml");

        DataSource data = service.DeCanonise(processId, version, name, cpf, anf);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

}
