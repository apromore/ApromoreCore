/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.impl;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

import org.apromore.anf.AnnotationsType;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.CanonicalRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.NativeTypeRepository;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Canonical;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.service.FormatService;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.util.StreamUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the FormatService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class FormatServiceImpl implements FormatService {

    private AnnotationRepository annotationRepo;
    private CanonicalRepository canonicalRepo;
    private NativeRepository nativeRepo;
    private NativeTypeRepository nativeTypeRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationRepository Annotation Repository.
     * @param nativeRepository Native Repository.
     * @param nativeTypeRepository Native Type repository.
     */
    @Inject
    public FormatServiceImpl(final AnnotationRepository annotationRepository, final CanonicalRepository canonicalRepository,
            final NativeRepository nativeRepository, final NativeTypeRepository nativeTypeRepository) {
        annotationRepo = annotationRepository;
        canonicalRepo = canonicalRepository;
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
    public List<NativeType> findAllFormats() {
        return nativeTypeRepo.findAll();
    }


    /**
     * @see org.apromore.service.FormatService#findNativeType(String)
     * {@inheritDoc}
     * <p/>
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    public NativeType findNativeType(String nativeType) {
        return nativeTypeRepo.findNativeType(nativeType);
    }

    /**
     * @see org.apromore.service.FormatService#storeNative(String, org.apromore.dao.model.ProcessModelVersion, String, String, org.apromore.dao.model.User, org.apromore.dao.model.NativeType, String, org.apromore.service.model.CanonisedProcess)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void storeNative(String procName, ProcessModelVersion pmv, String created, String lastUpdate, User user,
            NativeType nativeType, String annVersion, CanonisedProcess cp) throws JAXBException, IOException {
        //InputStream sync_npf = StreamUtil.copyParam2NPF(cpf, nativeType.getNatType(), procName, pmv.getVersionNumber(), user.getUsername(), created, lastUpdate);
        Native nat = null;

        if (cp.getOriginal() != null) {
            cp.getOriginal().reset();
            String nativeString = StreamUtil.inputStream2String(cp.getOriginal()).trim();
            nat = createNative(pmv, nativeType, nativeString);
            nat.setLastUpdateDate(lastUpdate);
        }

        String canonicalString = StreamUtil.inputStream2String(cp.getCpf()).trim();
        Canonical can = createCanonical(pmv, canonicalString);

        pmv.setNativeDocument(nat);
        pmv.setCanonicalDocument(can);

        if (!isEmptyANF(cp.getAnt())) {
            String annString = StreamUtil.inputStream2String(cp.getAnf()).trim();
            if (annString != null && !annString.equals("")) {
                Annotation annotation = new Annotation();
                annotation.setContent(annString);
                annotation.setName(annVersion);
                annotation.setNatve(nat);
                annotation.setProcessModelVersion(pmv);
                annotation = annotationRepo.save(annotation);

                pmv.getAnnotations().add(annotation);
            }
        }
    }


    private boolean isEmptyANF(AnnotationsType ant) {
        return ant == null || ant.getAnnotation() == null || ant.getAnnotation().isEmpty();
    }

    private Canonical createCanonical(ProcessModelVersion pmv, String canonicalString) {
        Canonical canonical = new Canonical();
        canonical.setContent(canonicalString);
        canonical.setProcessModelVersion(pmv);
        canonicalRepo.save(canonical);
        return canonical;
    }

    private Native createNative(ProcessModelVersion pmv, NativeType nativeType, String nativeString) {
        Native nat = new Native();
        nat.setNativeType(nativeType);
        nat.setContent(nativeString);
        nat.setProcessModelVersion(pmv);
        nat = nativeRepo.save(nat);
        return nat;
    }

}
