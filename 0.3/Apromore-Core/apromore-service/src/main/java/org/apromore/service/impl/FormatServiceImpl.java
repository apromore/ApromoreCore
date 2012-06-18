package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.AnnotationDao;
import org.apromore.dao.NativeDao;
import org.apromore.dao.NativeTypeDao;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.service.FormatService;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.util.StreamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.List;

/**
 * Implementation of the FormatService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("FormatService")
@Transactional(propagation = Propagation.REQUIRED)
public class FormatServiceImpl implements FormatService {

    @Autowired @Qualifier("AnnotationDao")
    private AnnotationDao annDao;
    @Autowired @Qualifier("NativeDao")
    private NativeDao natDao;
    @Autowired @Qualifier("NativeTypeDao")
    private NativeTypeDao natTypeDao;


    /**
     * @see org.apromore.service.FormatService#findAllFormats()
     * {@inheritDoc}
     *
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    @Transactional(readOnly = true)
    public List<NativeType> findAllFormats() {
        return natTypeDao.findAllFormats();
    }


    /**
     * @see org.apromore.service.FormatService#findNativeType(String)
     * {@inheritDoc}
     *
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    @Transactional(readOnly = true)
    public NativeType findNativeType(String nativeType) {
        return natTypeDao.findNativeType(nativeType);
    }

    /**
     * @see org.apromore.service.ProcessService#storeNative(String, String, org.apromore.dao.model.ProcessModelVersion, java.io.InputStream, String, String, org.apromore.dao.model.User, org.apromore.dao.model.NativeType, org.apromore.service.model.CanonisedProcess)
     * {@inheritDoc}
     */
    @Override
    public void storeNative(String procName, String version, ProcessModelVersion pmv, InputStream cpf, String created, String lastUpdate, User user,
            NativeType nativeType, CanonisedProcess cp) throws JAXBException {
        InputStream sync_npf = StreamUtil.copyParam2NPF(cpf, nativeType.getNatType(), procName, version, user.getUsername(), created, lastUpdate);
        String nativeString = StreamUtil.inputStream2String(sync_npf).trim();
        String annString = StreamUtil.inputStream2String(cp.getAnf()).trim();

        Native nat = new Native();
        nat.setNativeType(nativeType);
        nat.setContent(nativeString);
        nat.setProcessModelVersion(pmv);
        natDao.save(nat);

        Annotation annotation = new Annotation();
        annotation.setContent(annString);
        annotation.setProcessModelVersion(pmv);
        annotation.setName(Constants.INITIAL_ANNOTATION);
        annotation.setNatve(nat);
        annotation.setProcessModelVersion(pmv);
        annDao.save(annotation);
    }




    /**
     * Set the Annotation DAO object for this class. Mainly for spring tests.
     * @param annDAOJpa the Annotation Dao.
     */
    public void setAnnotationDao(AnnotationDao annDAOJpa) {
        annDao = annDAOJpa;
    }

    /**
     * Set the Native DAO object for this class. Mainly for spring tests.
     * @param natDAOJpa the Native Dao.
     */
    public void setNativeDao(NativeDao natDAOJpa) {
        natDao = natDAOJpa;
    }

    /**
     * Set the Native Type DAO object for this class. Mainly for spring tests.
     * @param nativeTypeDAOJpa the user Dao.
     */
    public void setNativeTypeDao(NativeTypeDao nativeTypeDAOJpa) {
        natTypeDao = nativeTypeDAOJpa;
    }
}
