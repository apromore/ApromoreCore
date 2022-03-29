/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import org.apromore.dao.NativeRepository;
import org.apromore.dao.NativeTypeRepository;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.service.FormatService;
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
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class FormatServiceImpl implements FormatService {
    private NativeRepository nativeRepo;
    private NativeTypeRepository nativeTypeRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationRepository Annotation Repository.
     * @param nativeRepository Native Repository.
     * @param nativeTypeRepository Native Type repository.
     */
    @Inject
    public FormatServiceImpl(final NativeRepository nativeRepository, final NativeTypeRepository nativeTypeRepository) {
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
    public Native storeNative(String procName, String created, String lastUpdate, User user,
            NativeType nativeType, String annVersion, InputStream original) throws IOException {
        Native nat = null;

        if (original != null) {
            String nativeString = StreamUtil.inputStream2String(original).trim();
            nat = createNative(nativeType, nativeString);
            nat.setLastUpdateDate(lastUpdate);
        }

        return nat;
    }


    private Native createNative( NativeType nativeType, String nativeString) {
        Native nat = new Native();
        nat.setNativeType(nativeType);
        nat.setContent(nativeString);        
        nat = nativeRepo.save(nat);
        return nat;
    }

    @Override
    public void updateNative(Native nativeData) {
        nativeRepo.save(nativeData);
    }
}
