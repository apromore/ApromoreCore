/*
 * Copyright © 2009-2017 The Apromore Initiative.
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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalResourceConversionContext;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.canoniser.yawl.internal.utils.YAWLConstants;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.HumanTypeEnum;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ObjectFactory;
import org.yawlfoundation.yawlschema.ResourcingAllocateFactsType;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType.InitialSet;
import org.yawlfoundation.yawlschema.ResourcingFactsType;
import org.yawlfoundation.yawlschema.ResourcingInitiatorType;
import org.yawlfoundation.yawlschema.ResourcingInteractionInitiatorType;
import org.yawlfoundation.yawlschema.ResourcingOfferFactsType;
import org.yawlfoundation.yawlschema.ResourcingSecondaryFactsType;
import org.yawlfoundation.yawlschema.ResourcingSelectorFactsType;

/**
 * Helps TaskTypeHandler to convert Resources
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class TaskResourcingHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskResourcingHelper.class);

    private static final ObjectFactory YAWL_FACTORY = new ObjectFactory();

    private final CanonicalConversionContext context;

    public TaskResourcingHelper(final CanonicalConversionContext context) {
        super();
        this.context = context;
    }

    public ResourcingFactsType convertResourceing(final TaskType task) {
        final ResourcingFactsType resourceing = YAWL_FACTORY.createResourcingFactsType();
        convertOffer(resourceing, task);
        convertStart(resourceing, task);
        convertAllocation(resourceing, task);
        return resourceing;
    }

    private void convertOffer(final ResourcingFactsType resourceing, final TaskType task) {
        ResourcingOfferFactsType extensionOffer = ExtensionUtils.getFromNodeExtension(task, ExtensionUtils.OFFER, ResourcingOfferFactsType.class, null);
        if (extensionOffer != null && extensionOffer.getInitiator().equals(ResourcingInitiatorType.USER)) {
            resourceing.setOffer(extensionOffer);
        } else {
            final ResourcingOfferFactsType offer = YAWL_FACTORY.createResourcingOfferFactsType();
            offer.setInitiator(ResourcingInitiatorType.SYSTEM);

            final ResourcingDistributionSetFactsType distributionSet = YAWL_FACTORY.createResourcingDistributionSetFactsType();
            final InitialSet initialDistributionSet = YAWL_FACTORY.createResourcingDistributionSetFactsTypeInitialSet();

            final List<ResourceTypeRefType> resourceRefList = task.getResourceTypeRef();

            Set<HumanType> primaryResources = findPrimaryResources(resourceRefList);
            Set<ResourceTypeType> secondaryResources = findSecondaryResources(resourceRefList, primaryResources);

            if (resourceRefList.size() > 1) {
                // All Resources are part of our initial distribution set
                for (HumanType humanType: primaryResources) {
                    addPrimaryResource(humanType, initialDistributionSet);
                }
                for (ResourceTypeType resource: secondaryResources) {
                    addSecondaryResource(resource, resourceing.getSecondary());
                }
            } else if (resourceRefList.size() == 1) {
                // Singleton distribution set
                final ResourceTypeRefType resourceReference = resourceRefList.get(0);
                final ResourceTypeType resourceType = getContext().getResourceTypeById(resourceReference.getResourceTypeId());
                if (resourceType != null) {
                    if (resourceType instanceof HumanType) {
                        addPrimaryResource((HumanType)resourceType, initialDistributionSet);
                    } else {
                        getContext().getMessageInterface().addMessage("Found non-human resource {0} in non-automatic Task {1}!", resourceType.getName(), task.getId());
                    }
                } else {
                    LOGGER.warn("Could not find ResourceType with ID {}! Invalid CPF!", resourceReference.getResourceTypeId());
                }
            }
            distributionSet.setInitialSet(initialDistributionSet);
            offer.setDistributionSet(distributionSet);
            resourceing.setOffer(offer);
        }
    }

    private void addSecondaryResource(final ResourceTypeType resource, final ResourcingSecondaryFactsType secondary) {
        //TODO secondary
    }

    private void addPrimaryResource(final HumanType humanType, final InitialSet initialDistributionSet) {
        if (humanType.getType() == null) {
            // Assume Role
            initialDistributionSet.getRole().add(getResourceContext().getConvertedRole(humanType.getId()).getId());
        } else if (humanType.getType().equals(HumanTypeEnum.ROLE)) {
            initialDistributionSet.getRole().add(getResourceContext().getConvertedRole(humanType.getId()).getId());
        } else if (humanType.getType().equals(HumanTypeEnum.PARTICIPANT)) {
            initialDistributionSet.getParticipant().add(getResourceContext().getConvertedParticipant(humanType.getId()).getId());
        } else {
            // Do not add directly in YAWL
            getContext().getMessageInterface().addMessage("Can not add resource {0} of type {1} to YAWL Task", humanType.getName(), humanType.getType());
        }
    }

    private void convertStart(final ResourcingFactsType resourceing, final TaskType task) {
        final ResourcingInteractionInitiatorType defaultStart = YAWL_FACTORY.createResourcingInteractionInitiatorType();
        defaultStart.setInitiator(ResourcingInitiatorType.USER);
        resourceing.setStart(ExtensionUtils.getFromNodeExtension(task, ExtensionUtils.START, ResourcingInteractionInitiatorType.class, defaultStart));
    }

    private void convertAllocation(final ResourcingFactsType resourceing, final TaskType task) {
        if (task.getAllocationStrategy() != null) {
            final ResourcingAllocateFactsType allocate = YAWL_FACTORY.createResourcingAllocateFactsType();
            allocate.setInitiator(ResourcingInitiatorType.SYSTEM);
            ResourcingSelectorFactsType allocator = YAWL_FACTORY.createResourcingSelectorFactsType();
            switch (task.getAllocationStrategy()) {
            case SHORTEST_QUEUE:
                allocator.setName(YAWLConstants.YAWL_ALLOCATOR_SHORTEST_QUEUE);
                break;
            case RANDOM:
                allocator.setName(YAWLConstants.YAWL_ALLOCATOR_RANDOM);
                break;
            case ROUND_ROBIN_BY_TIME:
                allocator.setName(YAWLConstants.YAWL_ALLOCATOR_ROUND_ROBIN_BY_TIME);
                break;
            case ROUND_ROBIN_BY_EXPERIENCE:
                allocator.setName(YAWLConstants.YAWL_ALLOCATOR_ROUND_ROBIN_BY_EXPERIENCE);
                break;
            case ROUND_ROBIN_BY_FREQUENCY:
                allocator.setName(YAWLConstants.YAWL_ALLOCATOR_ROUND_ROBIN_BY_LEAST_FREQUENCY);
                break;
            case OTHER:
                allocator.setName(YAWLConstants.YAWL_ALLOCATOR_RANDOM);
                break;
            }
            allocate.setAllocator(allocator);
            resourceing.setAllocate(allocate);
        } else {
            final ResourcingAllocateFactsType allocate = YAWL_FACTORY.createResourcingAllocateFactsType();
            allocate.setInitiator(ResourcingInitiatorType.USER);
            resourceing.setAllocate(ExtensionUtils.getFromNodeExtension(task, ExtensionUtils.ALLOCATE, ResourcingAllocateFactsType.class, allocate));
        }
    }

    private Set<ResourceTypeType> findSecondaryResources(final List<ResourceTypeRefType> resourceRefList, final Set<HumanType> primaryResources) {
        Set<ResourceTypeType> secondaryResources = new HashSet<ResourceTypeType>();
        for (ResourceTypeRefType ref: resourceRefList) {
            if (ref.getQualifier() != null && ref.getQualifier().equals("Secondary")) {
                ResourceTypeType resource = getContext().getResourceTypeById(ref.getResourceTypeId());
                if (resource != null) {
                    secondaryResources.add(resource);
                }
            }
        }
        // Fallback add all remaining resource, that are not in the in the Set of primary resources
        if (secondaryResources.isEmpty() && !resourceRefList.isEmpty()) {
            for (ResourceTypeRefType ref: resourceRefList) {
                ResourceTypeType resource = getContext().getResourceTypeById(ref.getResourceTypeId());
                if (resource != null && !primaryResources.contains(resource)) {
                    secondaryResources.add(resource);
                }
            }
        }
        return secondaryResources;
    }

    private Set<HumanType> findPrimaryResources(final List<ResourceTypeRefType> resourceRefList) {
        Set<HumanType> primaryResources = new HashSet<HumanType>();
        for (ResourceTypeRefType ref: resourceRefList) {
            if (ref.getQualifier() != null && ref.getQualifier().equals("Primary")) {
                ResourceTypeType resource = getContext().getResourceTypeById(ref.getResourceTypeId());
                if (resource instanceof HumanType) {
                    primaryResources.add((HumanType) resource);
                } else {
                    getContext().getMessageInterface().addMessage("Found primary non-human resource {0}! Invalid!", ref.getResourceTypeId());
                }
            }
        }
        // Fallback to the first resource if it is of HumanType
        if (primaryResources.isEmpty() && !resourceRefList.isEmpty()) {
            ResourceTypeType resource = getContext().getResourceTypeById(resourceRefList.get(0).getResourceTypeId());
            if (resource instanceof HumanType) {
                primaryResources.add((HumanType) resource);
            }
        } else {
            if (primaryResources.isEmpty()) {
                getContext().getMessageInterface().addMessage("Can not find any primary resource!");   
            }
        }
        return primaryResources;
    }

    public CanonicalConversionContext getContext() {
        return context;
    }

    public CanonicalResourceConversionContext getResourceContext() {
        return context.getResourceContext();
    }

}
