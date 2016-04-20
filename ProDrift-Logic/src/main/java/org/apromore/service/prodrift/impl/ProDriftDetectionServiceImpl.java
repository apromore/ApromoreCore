/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.prodrift.impl;

import ee.ut.eventstr.model.ProDriftDetectionResult;
import ee.ut.eventstr.test.AlphaBasedPosetReaderTest;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.plugin.provider.PluginProvider;
import org.apromore.service.CanoniserService;
import org.apromore.service.ProcessService;
import org.apromore.service.prodrift.ProDriftDetectionException;
import org.apromore.service.prodrift.ProDriftDetectionService;
import org.apromore.service.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 * Implementation of the ProDriftDetectionService Contract.
 *
 * @author barca
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ProDriftDetectionServiceImpl implements ProDriftDetectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProDriftDetectionServiceImpl.class);

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     */
    public ProDriftDetectionServiceImpl() {}

    /**
     * @see ProDriftDetectionService#proDriftDetector(byte[], int, String, String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public ProDriftDetectionResult proDriftDetector(byte[] logByteArray, int winSize, String fWinorAwin, String logFileName) throws ProDriftDetectionException {

        InputStream is = new ByteArrayInputStream(logByteArray);

        AlphaBasedPosetReaderTest driftTest0 = new AlphaBasedPosetReaderTest(is, logFileName, winSize, fWinorAwin);

        ProDriftDetectionResult pddRes = driftTest0.proDriftRun();


        return pddRes;
    }


}
