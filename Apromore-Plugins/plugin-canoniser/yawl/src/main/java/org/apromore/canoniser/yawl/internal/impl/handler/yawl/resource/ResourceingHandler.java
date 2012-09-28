/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.resource;

import java.text.MessageFormat;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLConversionHandler;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.DistributionSetRef;
import org.apromore.cpf.DistributionSetType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ResourcingAllocateFactsType;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType.Constraints;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType.Filters;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType.InitialSet;
import org.yawlfoundation.yawlschema.ResourcingFactsType;
import org.yawlfoundation.yawlschema.ResourcingInitiatorType;
import org.yawlfoundation.yawlschema.ResourcingInteractionInitiatorType;
import org.yawlfoundation.yawlschema.ResourcingOfferFactsType;
import org.yawlfoundation.yawlschema.ResourcingSecondaryFactsType;
import org.yawlfoundation.yawlschema.orgdata.ParticipantType;
import org.yawlfoundation.yawlschema.orgdata.RoleType;

/**
 * Converting YAWL resources to CPF
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class ResourceingHandler extends YAWLConversionHandler<ResourcingFactsType, TaskType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceingHandler.class);

    private static final String DISTRIBUTION_SET_RESOURCE_NAME = "Distribution Set";

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        if (isUserOffering(getObject())) {

            // Distribution of work will be handled by User at Runtime, there is no way of capturing this in CPF
            if (getObject().getOffer() != null) {
                ExtensionUtils.addToExtensions(ExtensionUtils.marshalYAWLFragment(ExtensionUtils.OFFER, getObject(), ResourcingFactsType.class),
                        getConvertedParent());
            }

        } else {
            if (hasDistributionSet(getObject())) {
                final ResourcingOfferFactsType offer = getObject().getOffer();
                final ResourcingAllocateFactsType allocate = getObject().getAllocate();

                ResourceTypeType convertedDSet = convertOffer(offer);

                if (convertedDSet != null) {
                    convertAllocation(allocate, convertedDSet);

                    // Add YAWL specific information about start
                    ExtensionUtils.addToExtensions(ExtensionUtils.marshalYAWLFragment(ExtensionUtils.START, getObject().getStart(), ResourcingInteractionInitiatorType.class),
                            convertedDSet);
                }

            } else {
                convertWithoutDistributionSet(getObject());
            }

            // Add Secondary Participant directly as Reference
            if (getObject().getSecondary() != null) {
                convertSecondaryResources(getObject().getSecondary());
            }
        }
    }

    private boolean isUserOffering(final ResourcingFactsType resourcing) {
        return resourcing.getOffer() == null || resourcing.getOffer().getInitiator().equals(ResourcingInitiatorType.USER);
    }

    private boolean hasDistributionSet(final ResourcingFactsType object) {
        ResourcingOfferFactsType offer = getObject().getOffer();
        final ResourcingAllocateFactsType allocate = getObject().getAllocate();

        if (offer != null && offer.getInitiator().equals(ResourcingInitiatorType.SYSTEM)) {
            ResourcingDistributionSetFactsType dSet = offer.getDistributionSet();
            if (hasManyResources(dSet.getInitialSet()) || hasFilter(dSet) || hasConstraints(dSet) || hasAllocator(allocate)) {
                return true;
            }
        }

        // There is just a single resource without any complex conditions
        return false;
    }

    private boolean hasAllocator(final ResourcingAllocateFactsType allocate) {
        return allocate != null && allocate.getInitiator().equals(ResourcingInitiatorType.SYSTEM);
    }

    private ResourceTypeType convertOffer(final ResourcingOfferFactsType offer) throws CanoniserException {

        ResourcingDistributionSetFactsType dSet = offer.getDistributionSet();

        if (dSet != null && (hasAtLeastOneResource(dSet.getInitialSet()) || hasFilter(dSet) || hasConstraints(dSet))) {
            return convertWithDistributionSet(dSet);
        } else {
            getContext().getMessageInterface().addMessage(MessageFormat.format(
                    "Empty distribution set, but filter/constraints/allocationStrategy defined. Can not convert resource information of task {0}!",
                    getConvertedParent().getId()));
            return null;
        }
    }

    private ResourceTypeType convertWithDistributionSet(final ResourcingDistributionSetFactsType distSet) throws CanoniserException {

        List<String> participantList = distSet.getInitialSet().getParticipant();
        List<String> roleList = distSet.getInitialSet().getRole();

        final ResourceTypeType resourceType = createDistributionSet();
        createResourceReference(resourceType, null);

        for (final String participantId : participantList) {
            final ResourceTypeType resource = createResourceTypeForParticipant(getContext().getParticipantById(participantId));
            final DistributionSetRef ref = CPF_FACTORY.createDistributionSetRef();
            ref.setResourceTypeId(resource.getId());
            resourceType.getDistributionSet().getResourceTypeRef().add(ref);
        }

        for (final String roleId : roleList) {
            final ResourceTypeType resource = createResourceTypeForRole(getContext().getRoleById(roleId));
            final DistributionSetRef ref = CPF_FACTORY.createDistributionSetRef();
            ref.setResourceTypeId(resource.getId());
            resourceType.getDistributionSet().getResourceTypeRef().add(ref);
        }

        if (hasFilter(distSet)) {
            convertFilter(distSet.getFilters());
        }

        if (hasConstraints(distSet)) {
            convertConstraints(distSet.getConstraints());
        }

        return resourceType;

    }

    private void convertAllocation(final ResourcingAllocateFactsType allocate, final ResourceTypeType distributionSet) throws CanoniserException {
        if (allocate != null && allocate.getInitiator().equals(ResourcingInitiatorType.SYSTEM)) {
            convertAllocator(allocate, distributionSet);
        } else {
         // Allocation of work will be handled by User at Runtime, there is no way of capturing this in CPF
            if (allocate != null) {
                ExtensionUtils.addToExtensions(ExtensionUtils.marshalYAWLFragment(ExtensionUtils.ALLOCATE, getObject().getOffer(), ResourcingOfferFactsType.class),
                        getConvertedParent());
            }
        }
    }

    private void convertAllocator(final ResourcingAllocateFactsType allocate, final ResourceTypeType distributionSet) {
        if (distributionSet != null) {

        }
    }

    private void convertConstraints(final Constraints constraints) {
        LOGGER.error("Should convert constraints " + constraints.toString());
    }

    private void convertFilter(final Filters filters) {
        LOGGER.error("Should convert filters " + filters.toString());
    }

    private void convertWithoutDistributionSet(final ResourcingFactsType resourcing) {

        if (resourcing.getOffer() != null && resourcing.getOffer().getDistributionSet() != null && resourcing.getOffer().getDistributionSet().getInitialSet() != null) {
            InitialSet initialSet = resourcing.getOffer().getDistributionSet().getInitialSet();

            List<String> participantList = initialSet.getParticipant();
            List<String> roleList = initialSet.getRole();

            // Add direct reference to Resource
            for (final String participantId : participantList) {
                final ResourceTypeType resource = createResourceTypeForParticipant(getContext().getParticipantById(participantId));
                createResourceReference(resource, null);
            }

            for (final String roleId : roleList) {
                final ResourceTypeType resource = createResourceTypeForRole(getContext().getRoleById(roleId));
                createResourceReference(resource, null);
            }

        }
    }

    private boolean hasConstraints(final ResourcingDistributionSetFactsType distributionSet) {
        return  distributionSet.getConstraints() != null;
    }

    private boolean hasFilter(final ResourcingDistributionSetFactsType distributionSet) {
        return distributionSet.getFilters() != null;
    }

    private boolean hasManyResources(final InitialSet initialSet) {
        return initialSet != null && initialSet.getParticipant().size() + initialSet.getRole().size() > 1;
    }


    private boolean hasAtLeastOneResource(final InitialSet initialSet) {
        return initialSet != null && initialSet.getParticipant().size() + initialSet.getRole().size() >= 1;
    }

    private void convertSecondaryResources(final ResourcingSecondaryFactsType secondaryResources) {
        for (final String participantId : secondaryResources.getParticipant()) {
            final ResourceTypeType resource = createResourceTypeForParticipant(getContext().getParticipantById(participantId));
            createResourceReference(resource, null);
        }

        // Add Secondary Roles directly as Reference
        for (final String roleId : secondaryResources.getRole()) {
            final ResourceTypeType resource = createResourceTypeForRole(getContext().getRoleById(roleId));
            createResourceReference(resource, null);
        }
        //TODO Non Human
    }

    /**
     * Creates a reference to a ResourceType.
     *
     * @param canonicalResource
     * @param qualifier
     */
    protected void createResourceReference(final ResourceTypeType canonicalResource, final String qualifier) {
        final ResourceTypeRefType resourceRef = CPF_FACTORY.createResourceTypeRefType();
        resourceRef.setId(generateUUID());
        resourceRef.setQualifier(qualifier);
        resourceRef.setResourceTypeId(canonicalResource.getId());
        getConvertedParent().getResourceTypeRef().add(resourceRef);
    }

    private ResourceTypeType createDistributionSet() {
        // Add reference to Distribution Set
        final ResourceTypeType resourceType = CPF_FACTORY.createResourceTypeType();
        resourceType.setId(generateUUID());
        resourceType.setName(DISTRIBUTION_SET_RESOURCE_NAME+" for "+getConvertedParent().getName());
        final DistributionSetType distributionSetExt = CPF_FACTORY.createDistributionSetType();
        resourceType.setDistributionSet(distributionSetExt);
        getContext().getCanonicalResult().getResourceType().add(resourceType);
        return resourceType;
    }

    private ResourceTypeType createResourceTypeForRole(final RoleType role) {
        ResourceTypeType canonicalResource = getContext().getGeneratedResourceType(role.getId());
        if (canonicalResource == null) {
            // Create a new ResourceType only for this Role
            canonicalResource = CPF_FACTORY.createRoleType();
            canonicalResource.setId(generateUUID(RESOURCE_ID_PREFIX, role.getId()));
            canonicalResource.setOriginalID(role.getId());
            canonicalResource.setName(role.getName());
            // Add new Resource as generated Resource
            getContext().setGeneratedResourceType(role.getId(), canonicalResource);
            // Add the ResourceType to the CanonicalProcess
            getContext().getCanonicalResult().getResourceType().add(canonicalResource);

            if (role.getBelongsToID() != null) {
                // Recursion is safe here, as we're keeping track of already converted Roles
                final ResourceTypeType parentRole = createResourceTypeForRole(role.getBelongsToID());
                parentRole.getSpecializationIds().add(canonicalResource.getId());
            }

        }
        return canonicalResource;
    }

    private ResourceTypeType createResourceTypeForParticipant(final ParticipantType participant) {
        ResourceTypeType canonicalResource = getContext().getGeneratedResourceType(participant.getId());
        if (canonicalResource == null) {
            // Create a new ResourceType only for this Role
            canonicalResource = CPF_FACTORY.createParticipantType();
            canonicalResource.setId(generateUUID(RESOURCE_ID_PREFIX, participant.getId()));
            canonicalResource.setOriginalID(participant.getId());
            canonicalResource.setName(participant.getFirstname() + " " + participant.getLastname());
            // Add new Resource as generated Resource
            getContext().setGeneratedResourceType(participant.getId(), canonicalResource);
            // Add the ResourceType to the CanonicalProcess
            getContext().getCanonicalResult().getResourceType().add(canonicalResource);

            if (participant.getRoles() != null) {
                // Add my Roles
                for (final JAXBElement<Object> role : participant.getRoles().getRole()) {
                    if (role.getValue() instanceof RoleType) {
                        final ResourceTypeType parentRole = createResourceTypeForRole((RoleType) role.getValue());
                        parentRole.getSpecializationIds().add(canonicalResource.getId());
                    } else {
                        LOGGER.warn("Wrong type of JAXBElement " + role.toString() + " expected RoleType");
                    }

                }
            }

        }
        return canonicalResource;
    }

}
