/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.csvimporter.impl;


import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementation of the CSVimporterService Contract.
 *
 * @author barca
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class CSVimporterServiceImpl implements CSVimporterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVimporterServiceImpl.class);

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     */
    public CSVimporterServiceImpl() {}

    /**
     * @see CSVimporterService#convertCSVtoXLog(File);
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public XLog convertCSVtoXLog(File csvFile) throws CSVimporterException {

        XLog xlog = null;



        return xlog;
    }


}
