package org.apromore.service.impl;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Date;

import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.SessionRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.EditSession;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.model.EditSessionType;
import org.apromore.service.SessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the FragmentService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
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
     * @see SessionService#readSession(Integer)
     * {@inheritDoc}
     */
    @Override
    public EditSession readSession(final Integer sessionCode) {
        EditSession session = sessionRepo.findOne(sessionCode);
        session.getProcess();
        session.getUser();
        return session;
    }

    /**
     * @see SessionService#deleteSession(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteSession(final Integer sessionCode) {
        sessionRepo.delete(sessionRepo.findOne(sessionCode));
    }


    /**
     * @see SessionService#createSession(org.apromore.model.EditSessionType)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public EditSession createSession(final EditSessionType editSession) throws ParseException {
        Integer processId = editSession.getProcessId();
        String branchName = editSession.getOriginalBranchName();
        Double versionNumber = editSession.getVersionNumber();
        Boolean withAnnotation = editSession.isWithAnnotation();

        ProcessModelVersion pmv = processModelVersionRepo.getProcessModelVersion(processId, branchName, versionNumber);

        EditSession session = new EditSession();
        session.setCreateDate(editSession.getCreationDate());
        session.setLastUpdateDate(editSession.getLastUpdate());
        session.setRecordTime(new Date());
        session.setVersionNumber(editSession.getVersionNumber());
        session.setOriginalBranchName(editSession.getOriginalBranchName());
        session.setNewBranchName(editSession.getNewBranchName());
        session.setCreateNewBranch(editSession.isCreateNewBranch());
        session.setNatType(editSession.getNativeType());
        session.setProcessModelVersion(pmv);
        session.setUser(userRepo.findByUsername(editSession.getUsername()));
        if (withAnnotation) {
            session.setAnnotation(editSession.getAnnotation());
        }

        return sessionRepo.save(session);
    }

}
