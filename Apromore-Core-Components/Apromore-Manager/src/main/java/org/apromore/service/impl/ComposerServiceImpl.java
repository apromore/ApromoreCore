/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.ComposerService;
import org.apromore.service.GraphService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;
import javax.inject.Inject;

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ComposerServiceImpl implements ComposerService {

    static private Logger LOGGER = Logger.getLogger(ComposerServiceImpl.class.getCanonicalName());

    @Inject
    private FragmentVersionRepository fragmentVersionRepository;
    @Inject
    private GraphService gService;



    /**
     * Compose a process Model graph from the DB.
     *
     * @param rootFragment the root Fragment we are going to build this model from.
     * @return the process model graph
     * @throws ExceptionDao if something fails.
     */
    @Override
    @Transactional(readOnly = false)
    public Canonical compose(final FragmentVersion rootFragment) throws ExceptionDao {

        if (rootFragment == null) {
            throw new ExceptionDao("Null argument passed as root fragment to composer service");
        }

        return composeFragment(rootFragment);
    }

    /**
     * Compose a process Model graph from the DB.
     *
     * @param rootFragmentId the root Fragment Id.
     * @return the process model graph
     * @throws ExceptionDao if something fails.
     */
    @Override
    @Transactional(readOnly = false)
    public Canonical compose(Integer rootFragmentId) throws ExceptionDao {
        return composeFragment(fragmentVersionRepository.findOne(rootFragmentId));
    }


    /* Compose a Fragment. */
    private Canonical composeFragment(FragmentVersion fv) {
        Canonical canonical = new Canonical();
        canonical = gService.fillNodesByFragment(canonical, fv.getUri());
        canonical = gService.fillEdgesByFragmentURINoError(canonical, fv.getUri());
        return canonical;
    }

}
