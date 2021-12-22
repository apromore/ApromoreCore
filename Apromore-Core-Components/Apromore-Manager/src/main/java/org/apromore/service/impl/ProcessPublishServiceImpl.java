/**
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import org.apromore.dao.ProcessPublishRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessPublish;
import org.apromore.service.ProcessPublishService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service("processPublishService")
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class ProcessPublishServiceImpl implements ProcessPublishService {
    private ProcessPublishRepository processPublishRepo;
    private ProcessRepository processRepo;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param processPublishRepository Process Publish Repository.
     */
    @Inject
    public ProcessPublishServiceImpl(final ProcessPublishRepository processPublishRepository,
                                     final ProcessRepository processRepository) {
        processPublishRepo = processPublishRepository;
        processRepo  = processRepository;
    }

    @Override
    public ProcessPublish savePublishDetails(int processId, String publishId, boolean publishStatus) {
        Process process = processRepo.findUniqueByID(processId);
        if (process == null) {
            throw new IllegalArgumentException("No process could be found with the id " + publishId);
        }

        ProcessPublish processPublish = new ProcessPublish();
        processPublish.setPublishId(publishId);
        processPublish.setPublished(publishStatus);
        processPublish.setProcess(process);

        return processPublishRepo.saveAndFlush(processPublish);
    }

    @Override
    public ProcessPublish updatePublishStatus(String publishId, boolean publishStatus) {
        ProcessPublish processPublish = processPublishRepo.findByPublishId(publishId);
        processPublish.setPublished(publishStatus);

        return processPublishRepo.saveAndFlush(processPublish);
    }

    @Override
    public ProcessPublish getPublishDetails(int processId) {
        return processPublishRepo.findByProcessId(processId);
    }
}
