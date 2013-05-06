package org.apromore.service.impl;

import org.apromore.dao.SessionRepository;
import org.apromore.dao.model.EditSession;
import org.apromore.model.EditSessionType;
import org.apromore.service.SessionService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit test the Session Service Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Ignore
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(value = DependencyInjectionTestExecutionListener.class)
public class SessionServiceImplIntgTest {

    @Inject
    private SessionService sSrv;
    @Inject
    private SessionRepository sRepo;

    @Test
    @Rollback(true)
    public void testStandardImportProcess() throws Exception {
        EditSession ses = createSession();

        EditSessionType sessionType = new EditSessionType();
        sessionType.setAnnotation("XPDL");
        sessionType.setCreationDate("12/12/2012 10:00:00AM");
        sessionType.setLastUpdate("12/12/2012 10:00:00AM");
        sessionType.setNativeType("XPDL 2.1");
        sessionType.setProcessId(1);
        sessionType.setProcessName("TEST");
        sessionType.setWithAnnotation(Boolean.FALSE);

        EditSession es = sSrv.createSession(sessionType);

        assertThat(es.getId(), notNullValue());
        assertThat(es.getId(), equalTo(1));
    }

    private EditSession createSession() {
        EditSession session = new EditSession();
        session.setAnnotation("");
        session.setNatType("XPDL 1.2");
        session.setRemoveFakeEvents(Boolean.TRUE);
        session.setVersionNumber(1.0);
        return sRepo.save(session);
    }


}
