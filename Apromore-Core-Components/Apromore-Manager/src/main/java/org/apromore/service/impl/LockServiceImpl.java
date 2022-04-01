/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

import javax.inject.Inject;

import org.apromore.common.Constants;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.service.LockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the LockService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class LockServiceImpl implements LockService {
    private ProcessModelVersionRepository processModelVersionRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param fragmentVersionRepository Fragment Version Repository.
     * @param fragmentVersionDagRepository Fragment Version Dag Repository.
     * @param processModelVersionRepository Process Model Version repository.
     */
    @Inject
    public LockServiceImpl(final ProcessModelVersionRepository processModelVersionRepository) {
        processModelVersionRepo = processModelVersionRepository;
    }



    /**
     * @see org.apromore.service.LockService#lockProcessModelVersion(Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public boolean lockProcessModelVersion(Integer processModelVersionId) {
        ProcessModelVersion result = null;
        ProcessModelVersion prsModelVersion = processModelVersionRepo.findById(processModelVersionId).get();

        if (prsModelVersion.getLockStatus().equals(Constants.NO_LOCK)) {
            prsModelVersion.setLockStatus(Constants.DIRECT_LOCK);
            result = processModelVersionRepo.save(prsModelVersion);
        }

        assert result != null;
        return !result.getLockStatus().equals(Constants.NO_LOCK);
    }

    /**
     * @see org.apromore.service.LockService#unlockProcessModelVersion(ProcessModelVersion)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void unlockProcessModelVersion(ProcessModelVersion processModelVersion) {
        processModelVersion.setLockStatus(Constants.NO_LOCK);
        processModelVersionRepo.save(processModelVersion);
    }
}
