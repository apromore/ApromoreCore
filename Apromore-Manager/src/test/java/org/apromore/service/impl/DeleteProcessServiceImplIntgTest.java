package org.apromore.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.ProcessService;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.NameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Unit test the ProcessService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class DeleteProcessServiceImplIntgTest {

    @Inject
    private CanoniserService cSrv;
    @Inject
    private ProcessService pSrv;
    @PersistenceContext
    private EntityManager em;

    private String username = "james";
    private String domain = "Tests";
    private String created = "12/12/2011";
    private String lastUpdate = "12/12/2011";



    @Test
    @Rollback(true)
    public void testImportThenDeleteModel() throws Exception {
        String natType = "EPML 2.0";
        String name = "AudioTest1";
        String branch = "MAIN";

        // Insert Process
        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/Test1.epml"), "text/xml"));
        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>(0));
        ProcessModelVersion pst = pSrv.importProcess(username, 0, name, 1.0d, natType, cp, domain, "", created, lastUpdate);
        assertThat(pst, notNullValue());
        em.flush();

        // Delete Process
        List<NameValuePair> deleteList = new ArrayList<>(0);
        deleteList.add(new NameValuePair(name, branch));
        pSrv.deleteProcessModel(deleteList);
        em.flush();

        // Try and Find it again
        CanonicalProcessType cpt = pSrv.getCurrentProcessModel(name, branch, false);
        assertThat(cpt, nullValue());
    }


    @Test
    @Rollback(true)
    public void testImportUpdateThenDeleteModel() throws Exception {
        String natType = "EPML 2.0";
        String name = "AudioTest2";
        String branch = "MAIN";

        // Insert Process
        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/Test2.epml"), "text/xml"));
        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>(0));
        ProcessModelVersion pst = pSrv.importProcess(username, 0, name, 1.0d, natType, cp, domain, "", created, lastUpdate);
        assertThat(pst, notNullValue());
        em.flush();

        // Update process
        stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/Test2.epml"), "text/xml"));
        cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>(0));

        // Delete Process
        List<NameValuePair> deleteList = new ArrayList<>(0);
        deleteList.add(new NameValuePair(name, branch));
        pSrv.deleteProcessModel(deleteList);
        em.flush();

        // Try and Find it again
        CanonicalProcessType cpt = pSrv.getCurrentProcessModel(name, branch, false);
        assertThat(cpt, nullValue());
    }

}
