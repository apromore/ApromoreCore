package org.apromore.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.FormatService;
import org.apromore.service.ProcessService;
import org.apromore.service.SecurityService;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.NameValuePair;
import org.junit.Ignore;
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
@Ignore
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class UpdateProcessServiceImplIntgTest {

    @Inject
    private CanoniserService cSrv;
    @Inject
    private ProcessService pSrv;
    @Inject
    private FormatService fSrv;
    @Inject
    private SecurityService sSrv;
    @PersistenceContext
    private EntityManager em;


    @Test
    @Rollback(true)
    public void testImportUpdateThenDeleteModel() throws Exception {
        String natType = "EPML 2.0";
        String name = "AudioTest2";
        String branch = "MAIN";

        NativeType nativeType = fSrv.findNativeType(natType);

        // Insert Process
        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/Audio.epml"), "text/xml"));
        CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>(0));
        String username = "james";
        String domain = "Tests";
        String lastUpdate = "12/12/2011";
        String created = "12/12/2011";
        ProcessModelVersion pst = pSrv.importProcess(username, 0, name, 1.0d, natType, cp, domain, "", created, lastUpdate);
        assertThat(pst, notNullValue());
        em.flush();

        // Update process
        stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/Audio.epml"), "text/xml"));
        cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>(0));
        User user = sSrv.getUserByName("james");
        pSrv.updateProcess(pst.getId(), name, branch, "testBranch", 1.1d, pst.getVersionNumber(), Boolean.FALSE, user, Constants.LOCKED, nativeType, cp);
        em.flush();

        // Delete Process
        List<NameValuePair> deleteList = new ArrayList<>(0);
        deleteList.add(new NameValuePair(name, branch));
        pSrv.deleteProcessModel(deleteList);
        em.flush();

        // Try and Find it again
        CanonicalProcessType cpt = pSrv.getCurrentProcessModel(name, branch, false);
        assertThat(cpt, notNullValue());
        assertThat(cpt.getNet().size(), equalTo(0));
        assertThat(cpt.getResourceType().size(), equalTo(0));
        assertThat(cpt.getDataTypes(), nullValue());
        assertThat(cpt.getUri(), nullValue());
        assertThat(cpt.getName(), nullValue());
        assertThat(cpt.getVersion(), nullValue());
    }

}
