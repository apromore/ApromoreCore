package org.apromore.service.impl;

import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.SessionRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.EditSession;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.model.EditSessionType;
import org.apromore.service.SessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import javax.inject.Inject;

/**
 * Implementation of the FragmentService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private ProcessModelVersionRepository processModelVersionRepo;
    private SessionRepository sessionRepo;
    private UserRepository userRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param processModelVersionRepository Process Model Version Repository.
     * @param sessionRepository Session Repository.
     * @param userRepository User repository.
     */
    @Inject
    public SessionServiceImpl(final ProcessModelVersionRepository processModelVersionRepository,
            final SessionRepository sessionRepository, final UserRepository userRepository) {
        processModelVersionRepo = processModelVersionRepository;
        sessionRepo = sessionRepository;
        userRepo = userRepository;
    }



    /**
     * @see SessionService#readSession(int)
     * {@inheritDoc}
     */
    @Override
    public EditSession readSession(final int sessionCode) {
        EditSession session = sessionRepo.findOne(sessionCode);
        session.getProcess();
        session.getUser();
        return session;
    }

    /**
     * @see SessionService#deleteSession(int)
     * {@inheritDoc}
     */
    @Override
    public void deleteSession(final int sessionCode) {
        sessionRepo.delete(sessionRepo.findOne(sessionCode));
    }


    /**
     * @see SessionService#createSession(org.apromore.model.EditSessionType)
     * {@inheritDoc}
     */
    @Override
    public EditSession createSession(final EditSessionType editSession) {
        int processId = editSession.getProcessId();
        String versionName = editSession.getVersionName();
        Boolean withAnnotation = editSession.isWithAnnotation();

        ProcessModelVersion pmv = processModelVersionRepo.getCurrentVersion(processId, versionName);

        EditSession session = new EditSession();
        session.setCreationDate(editSession.getCreationDate());
        session.setLastUpdate(editSession.getLastUpdate());
        session.setRecordTime(new Date());
        session.setVersionName(versionName);
        session.setNatType(editSession.getNativeType());
        session.setProcessModelVersion(pmv);
        session.setUser(userRepo.findByUsername(editSession.getUsername()));
        if (withAnnotation) {
            session.setAnnotation(editSession.getAnnotation());
        }

        return sessionRepo.save(session);
    }

}
