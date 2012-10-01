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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLConversionHandler;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.HumanTypeEnum;
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

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        TaskType task = getConvertedParent();

        // In YAWL it is not Team Work usually
        task.setTeamWork(false);

        if (!isSystemOffered(getObject())) {

            // Distribution of work will be handled by User at Runtime, there is no way of capturing this in CPF
            if (getObject().getOffer() != null) {
                ExtensionUtils
                        .addToExtensions(ExtensionUtils.marshalYAWLFragment(ExtensionUtils.OFFER, getObject(), ResourcingFactsType.class), task);
            }

        } else {

            List<HumanType> cpfResources = convertDistributionSet(getObject());
            convertAllocation(getObject().getAllocate(), task);

            final ResourcingOfferFactsType offer = getObject().getOffer();
            if (offer != null) {
                ResourcingDistributionSetFactsType distributionSet = offer.getDistributionSet();
                if (distributionSet != null) {
                    if (hasFilter(offer.getDistributionSet())) {
                        convertFilter(offer.getDistributionSet().getFilters(), task);
                    }
                    if (hasConstraints(offer.getDistributionSet())) {
                        convertConstraints(offer.getDistributionSet().getConstraints(), task);
                    }
                }
            }

            // Add YAWL specific information about start
            ExtensionUtils.addToExtensions(
                    ExtensionUtils.marshalYAWLFragment(ExtensionUtils.START, getObject().getStart(), ResourcingInteractionInitiatorType.class), task);

            // Add Secondary Participant directly as Reference
            if (getObject().getSecondary() != null) {
                convertSecondaryResources(getObject().getSecondary());
            }
        }
    }

    private boolean isSystemOffered(final ResourcingFactsType resourcing) {
        return resourcing.getOffer() == null || resourcing.getOffer().getInitiator().equals(ResourcingInitiatorType.SYSTEM);
    }

    private boolean isSystemAllocated(final ResourcingAllocateFactsType allocate) {
        return allocate != null && allocate.getInitiator().equals(ResourcingInitiatorType.SYSTEM);
    }

    private boolean hasConstraints(final ResourcingDistributionSetFactsType distributionSet) {
        return distributionSet.getConstraints() != null;
    }

    private boolean hasFilter(final ResourcingDistributionSetFactsType distributionSet) {
        return distributionSet.getFilters() != null;
    }

    private void convertAllocation(final ResourcingAllocateFactsType allocate, final TaskType task) throws CanoniserException {
        if (isSystemAllocated(allocate)) {
            // TODO
        } else {
            // Allocation of work will be handled by User at Runtime, there is no way of capturing this in CPF
            if (allocate != null) {
                ExtensionUtils.addToExtensions(
                        ExtensionUtils.marshalYAWLFragment(ExtensionUtils.ALLOCATE, getObject().getOffer(), ResourcingOfferFactsType.class),
                        task);
            }
        }
    }

    private void convertConstraints(final Constraints constraints, final TaskType task) {
        LOGGER.error("Should convert constraints " + constraints.toString());
        // TODO
    }

    private void convertFilter(final Filters filters, final TaskType task) {
        LOGGER.error("Should convert filters " + filters.toString());
        // TODO
    }

    private List<HumanType> convertDistributionSet(final ResourcingFactsType resourcing) {

        List<HumanType> convertedDistributionSet = new ArrayList<HumanType>();

        if (resourcing.getOffer() != null && resourcing.getOffer().getDistributionSet() != null
                && resourcing.getOffer().getDistributionSet().getInitialSet() != null) {
            InitialSet initialSet = resourcing.getOffer().getDistributionSet().getInitialSet();

            List<String> participantList = initialSet.getParticipant();
            List<String> roleList = initialSet.getRole();

            // Add direct reference to Resource
            for (final String participantId : participantList) {
                final HumanType resource = createResourceTypeForParticipant(getContext().getParticipantById(participantId));
                createResourceReference(resource, "Primary");
                convertedDistributionSet.add(resource);
            }

            for (final String roleId : roleList) {
                final HumanType resource = createResourceTypeForRole(getContext().getRoleById(roleId));
                createResourceReference(resource, "Primary");
                convertedDistributionSet.add(resource);
            }
        }

        return convertedDistributionSet;
    }

    private void convertSecondaryResources(final ResourcingSecondaryFactsType secondaryResources) {

        for (final String participantId : secondaryResources.getParticipant()) {
            final ResourceTypeType resource = createResourceTypeForParticipant(getContext().getParticipantById(participantId));
            createResourceReference(resource, "Secondary");
        }

        // Add Secondary Roles directly as Reference
        for (final String roleId : secondaryResources.getRole()) {
            final ResourceTypeType resource = createResourceTypeForRole(getContext().getRoleById(roleId));
            createResourceReference(resource, "Secondary");
        }

        // TODO Non Human
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

    /**
     * Creates a HumanType resources for a YAWL role
     *
     * @param role
     *            YAWL role
     * @return CPFs HumanType
     */
    private HumanType createResourceTypeForRole(final RoleType role) {
        HumanType canonicalResource = (HumanType) getContext().getGeneratedResourceType(role.getId());
        if (canonicalResource == null) {
            // Create a new ResourceType only for this Role
            canonicalResource = CPF_FACTORY.createHumanType();
            canonicalResource.setType(HumanTypeEnum.ROLE);
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

    /**
     * Creates a HumanType resources for a YAWL participant
     *
     * @param participant
     *            YAWL participant
     * @return HumanType
     */
    private HumanType createResourceTypeForParticipant(final ParticipantType participant) {
        HumanType canonicalResource = (HumanType) getContext().getGeneratedResourceType(participant.getId());
        if (canonicalResource == null) {
            // Create a new ResourceType only for this Role
            canonicalResource = CPF_FACTORY.createHumanType();
            canonicalResource.setType(HumanTypeEnum.PARTICIPANT);
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
