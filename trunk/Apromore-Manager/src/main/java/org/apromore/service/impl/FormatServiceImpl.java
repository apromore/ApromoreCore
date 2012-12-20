package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.NativeTypeRepository;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.service.FormatService;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.util.StreamUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

/**
 * Implementation of the FormatService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional
public class FormatServiceImpl implements FormatService {

    private AnnotationRepository annotationRepo;
    private NativeRepository nativeRepo;
    private NativeTypeRepository nativeTypeRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationRepository Annotation Repository.
     * @param nativeRepository Native Repository.
     * @param nativeTypeRepository Native Type repository.
     */
    @Inject
    public FormatServiceImpl(final AnnotationRepository annotationRepository, final NativeRepository nativeRepository,
            final NativeTypeRepository nativeTypeRepository) {
        annotationRepo = annotationRepository;
        nativeRepo = nativeRepository;
        nativeTypeRepo = nativeTypeRepository;
    }



    /**
     * @see org.apromore.service.FormatService#findAllFormats()
     *      {@inheritDoc}
     *      <p/>
     *      NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    @Transactional(readOnly = true)
    public List<NativeType> findAllFormats() {
        return nativeTypeRepo.findAll();
    }


    /**
     * @see org.apromore.service.FormatService#findNativeType(String)
     *      {@inheritDoc}
     *      <p/>
     *      NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    @Transactional(readOnly = true)
    public NativeType findNativeType(String nativeType) {
        return nativeTypeRepo.findNativeType(nativeType);
    }

    /**
     * @see org.apromore.service.FormatService#storeNative(String, String, org.apromore.dao.model.ProcessModelVersion, java.io.InputStream, String, String, org.apromore.dao.model.User, org.apromore.dao.model.NativeType, org.apromore.service.model.CanonisedProcess)
     *      {@inheritDoc}
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
        nat = nativeRepo.save(nat);

        Annotation annotation = new Annotation();
        annotation.setContent(annString);
        annotation.setName(Constants.INITIAL_ANNOTATION);
        annotation.setNatve(nat);
        annotation.setProcessModelVersion(pmv);
        annotation = annotationRepo.save(annotation);

        pmv.getNatives().add(nat);
        pmv.getAnnotations().add(annotation);
    }

}
