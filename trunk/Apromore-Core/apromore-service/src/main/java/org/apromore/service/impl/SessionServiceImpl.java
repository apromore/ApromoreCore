package org.apromore.service.impl;

import java.util.Date;

import org.apromore.dao.ProcessModelVersionDao;
import org.apromore.dao.SessionDao;
import org.apromore.dao.UserDao;
import org.apromore.dao.model.EditSession;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.model.EditSessionType;
import org.apromore.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the FragmentService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("SessionService")
@Transactional(propagation = Propagation.REQUIRED)
public class SessionServiceImpl implements SessionService {

    @Autowired @Qualifier("ProcessModelVersionDao")
    ProcessModelVersionDao pmvDao;
    @Autowired @Qualifier("UserDao")
    private UserDao usrDao;

    @Autowired @Qualifier("SessionDao")
    private SessionDao sessDao;


    /**
     * @see SessionService#readSession(int)
     * {@inheritDoc}
     */
    @Override
    public EditSession readSession(final int sessionCode) {
        EditSession session = sessDao.findSession(sessionCode);
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
        sessDao.delete(sessDao.findSession(sessionCode));
    }


    /**
     * @see SessionService#createSession(org.apromore.model.EditSessionType)
     * {@inheritDoc}
     */
    @Override
    public int createSession(final EditSessionType editSession) {
        int processId = editSession.getProcessId();
        String versionName = editSession.getVersionName();
        Boolean withAnnotation = editSession.isWithAnnotation();

        ProcessModelVersion pmv = pmvDao.getCurrentVersion(processId, versionName);

        EditSession session = new EditSession();
        session.setCreationDate(editSession.getCreationDate());
        session.setLastUpdate(editSession.getLastUpdate());
        session.setRecordTime(new Date());
        session.setVersionName(versionName);
        session.setNatType(editSession.getNativeType());
        session.setProcessModelVersion(pmv);
        session.setUser(usrDao.findUser(editSession.getUsername()));
        if (withAnnotation) {
            session.setAnnotation(editSession.getAnnotation());
        }

        sessDao.save(session);
        return session.getCode();
    }



    /**
     * Set the Process Model Version DAO object for this class. Mainly for spring tests.
     * @param pmvDAOJpa the process model version
     */
    public void setProcessModelVersionDao(ProcessModelVersionDao pmvDAOJpa) {
        pmvDao = pmvDAOJpa;
    }

    /**
     * Set the User DAO object for this class. Mainly for spring tests.
     * @param usrDAOJpa the user Dao.
     */
    public void setUserDao(UserDao usrDAOJpa) {
        usrDao = usrDAOJpa;
    }

    /**
     * Set the Session DAO object for this class. Mainly for spring tests.
     * @param sessDAOJpa the session Dao.
     */
    public void setSessionDao(SessionDao sessDAOJpa) {
        sessDao = sessDAOJpa;
    }
}
