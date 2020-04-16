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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;

import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.LockFailedException;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.ComposerService;
import org.apromore.service.FragmentService;
import org.apromore.service.LockService;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentDataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the FragmentService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class FragmentServiceImpl implements FragmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FragmentServiceImpl.class);

    private FragmentVersionRepository fvRepository;
    private FragmentVersionDagRepository fvdRepository;
    private CanonicalConverter converter;
    private ComposerService compService;
    private LockService lService;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param fragmentVersionRepository Fragment Version repository.
     * @param fragmentVersionDagRepository Fragment Version Dag repository.
     * @param canonicalConverter Canonical Converter.
     * @param composerService The composer Service
     * @param lockService Lock Service.
     */
    @Inject
    public FragmentServiceImpl(final FragmentVersionRepository fragmentVersionRepository,
            final FragmentVersionDagRepository fragmentVersionDagRepository, final LockService lockService,
            final @Qualifier("composerServiceImpl") ComposerService composerService,
            final CanonicalConverter canonicalConverter) {
        fvRepository = fragmentVersionRepository;
        fvdRepository = fragmentVersionDagRepository;
        converter = canonicalConverter;
        compService = composerService;
        lService = lockService;
    }



    /**
     * @see FragmentService#getFragmentToCanonicalProcessType(Integer)
     * {@inheritDoc}
     */
    @Override
    public CanonicalProcessType getFragmentToCanonicalProcessType(Integer fragmentId) {
        Canonical fragmentGraph;
        CanonicalProcessType tmp;
        CanonicalProcessType result = new CanonicalProcessType();
        try {
            fragmentGraph = getFragment(fragmentId, false);
            fragmentGraph.setProperty(Constants.ROOT_FRAGMENT_ID, fragmentId.toString());

            tmp = converter.convert(fragmentGraph);

            result.getNet().addAll(tmp.getNet());
            result.getResourceType().addAll(tmp.getResourceType());
            result.getAttribute().addAll(tmp.getAttribute());
        } catch (LockFailedException e) {
            String msg = "Failed to retrieve the Fragment " + fragmentId;
            LOGGER.error(msg, e);
        }
        return result;
    }

    /**
     * @see FragmentService#getFragment(Integer, boolean)
     * {@inheritDoc}
     */
    @Override
    public Canonical getFragment(Integer fragmentId, boolean lock) throws LockFailedException {
        Canonical processModelGraph = null;
        try {
            if (lock) {
                LOGGER.debug("Obtaining a lock for the fragment " + fragmentId + "...");
                boolean locked = lService.lockFragment(fragmentId);
                if (!locked) {
                    throw new LockFailedException();
                }
            }

            LOGGER.debug("Composing the fragment " + fragmentId + "...");
            FragmentVersion fv = fvRepository.findOne(fragmentId);
            processModelGraph = compService.compose(fv);
            processModelGraph.setProperty(Constants.ORIGINAL_FRAGMENT_ID, fragmentId.toString());

            if (lock) {
                processModelGraph.setProperty(Constants.LOCK_STATUS, Constants.LOCKED);
            }
        } catch (ExceptionDao e) {
            String msg = "Failed to retrieve the fragment " + fragmentId;
            LOGGER.error(msg, e);
            return processModelGraph;
        }

        return processModelGraph;
    }

    /**
     * @see FragmentService#getFragmentVersion(Integer)
     * {@inheritDoc}
     */
    @Override
    public FragmentVersion getFragmentVersion(Integer fragmentVersionId) {
        return fvRepository.findOne(fragmentVersionId);
    }

    /**
     * @see FragmentService#addFragmentVersion(org.apromore.dao.model.ProcessModelVersion, java.util.Map, String, int, int, int, String)
     */
    @Override
    @Transactional(readOnly = false)
    public FragmentVersion addFragmentVersion(ProcessModelVersion processModel, Map<String, String> childMappings,
            String derivedFrom, int lockStatus, int lockCount, int originalSize, String fragmentType) {
        String childMappingCode = calculateChildMappingCode(childMappings);

        FragmentVersion fragVersion = new FragmentVersion();
        fragVersion.setUri(UUID.randomUUID().toString());
        fragVersion.setChildMappingCode(childMappingCode);
        fragVersion.setDerivedFromFragment(derivedFrom);
        fragVersion.setLockStatus(lockStatus);
        fragVersion.setLockCount(lockCount);
        fragVersion.setFragmentType(fragmentType);
        fragVersion.setFragmentSize(originalSize);
        fragVersion.getProcessModelVersions().add(processModel) ;
        processModel.getFragmentVersions().add(fragVersion);

        return fvRepository.save(fragVersion);
    }

    /**
     * @see FragmentService#addFragmentVersionDag(java.util.Map)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void addFragmentVersionDag(Map<FragmentVersion, Map<String, String>> childMappings) {
        String childId;
        Set<String> pocketIds;
        Map<String, String> mappings;

        Set<FragmentVersion> parentIds = childMappings.keySet();
        for (FragmentVersion parent : parentIds) {
            mappings = childMappings.get(parent);

            pocketIds = mappings.keySet();
            for (String pocketId : pocketIds) {
                childId = mappings.get(pocketId);
                if (parent == null || childId == null || pocketId == null) {
                    LOGGER.error("Invalid child mapping parameters. child Id: " + childId + ", Pocket Id: " + pocketId);
                }

                FragmentVersionDag fvd = new FragmentVersionDag();
                fvd.setPocketId(pocketId);
                fvd.setFragmentVersion(parent);
                fvd.setChildFragmentVersion(fvRepository.findFragmentVersionByUri(childId));

                if (fvd.getChildFragmentVersion() == null) {
                    LOGGER.info("FragmentVersionDAG without a Child fragment version.....");
                }

                fvdRepository.save(fvd);
            }
        }
    }


    /**
     * @see FragmentService#getFragment(String, boolean)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public Canonical getFragment(final String fragmentUri, final boolean lock) throws LockFailedException {
        if (lock) {
            boolean locked = lService.lockFragmentByUri(fragmentUri);
            if (!locked) {
                throw new LockFailedException();
            }
        }

        Canonical processModelGraph = null;
        try {

            processModelGraph = compService.compose(fvRepository.findFragmentVersionByUri(fragmentUri));
            processModelGraph.setProperty(Constants.ORIGINAL_FRAGMENT_ID, fragmentUri);
            if (lock) {
                processModelGraph.setProperty(Constants.LOCK_STATUS, Constants.LOCKED);
            }
        } catch (ExceptionDao e) {
            String msg = "Failed to retrieve the fragment " + fragmentUri;
            LOGGER.error(msg, e);
        }
        return processModelGraph;
    }


    /**
     * @see FragmentService#getUnprocessedFragments()
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<FragmentDataObject> getUnprocessedFragments() {
        List<FragmentDataObject> fragments = new ArrayList<>();
        List<FragmentVersion> fvs = fvRepository.findAll();
        for (FragmentVersion fv : fvs) {
            FragmentDataObject fragment = new FragmentDataObject();
            fragment.setFragmentId(fv.getId());
            fragment.setSize(fv.getFragmentSize());
            fragments.add(fragment);
        }
        return fragments;
    }

    /**
     * @see FragmentService#getUnprocessedFragmentsOfProcesses(java.util.List)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<FragmentDataObject> getUnprocessedFragmentsOfProcesses(final List<Integer> processIds) {
        List<FragmentDataObject> fragments = new ArrayList<>();
        List<FragmentVersion> fvs = fvRepository.getFragmentsByProcessIds(processIds);
        for (FragmentVersion fv : fvs) {
            FragmentDataObject fragment = new FragmentDataObject();
            fragment.setFragmentId(fv.getId());
            fragment.setSize(fv.getFragmentSize());
            fragments.add(fragment);
        }
        return fragments;
    }



    private String calculateChildMappingCode(Map<String, String> childMapping) {
        StringBuilder buf = new StringBuilder();
        Set<String> pids = childMapping.keySet();
        PriorityQueue<String> q = new PriorityQueue<>(pids);
        while (!q.isEmpty()) {
            String pid = q.poll();
            String cid = childMapping.get(pid);
            buf.append(pid).append(":").append(cid).append("|");
        }
        return buf.toString();
    }

}
