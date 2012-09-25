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

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLConversionHandler;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.DistributionSetRef;
import org.apromore.cpf.DistributionSetType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType.Constraints;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType.Filters;
import org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType.InitialSet;
import org.yawlfoundation.yawlschema.ResourcingFactsType;
import org.yawlfoundation.yawlschema.ResourcingInitiatorType;
import org.yawlfoundation.yawlschema.ResourcingOfferFactsType;
import org.yawlfoundation.yawlschema.ResourcingOfferFactsType.FamiliarParticipant;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceingHandler.class.getName());

    private static final String DISTRIBUTION_SET_RESOURCE_NAME = "Distribution Set";

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        final ResourcingOfferFactsType offer = getObject().getOffer();

        if (offer != null && offer.getInitiator().equals(ResourcingInitiatorType.SYSTEM)) {

            if (offer.getDistributionSet() != null) {
                convertDistributionSet(offer.getDistributionSet());
            }

            if (offer.getFamiliarParticipant() != null) {
                ConversionUtils.addToExtensions(
                        ConversionUtils.marshalYAWLFragment("familiarParticipant", offer.getFamiliarParticipant(), FamiliarParticipant.class),
                        getConvertedParent());
            }

        } else {
            // Distribution of work will be handled by User at Runtime, there is no way of capturing this in CPF
            ConversionUtils.addToExtensions(ConversionUtils.marshalYAWLFragment("resourcing", getObject(), ResourcingFactsType.class),
                    getConvertedParent());
        }

        // TODO Deal with secondary resources in a more clever way
        ConversionUtils.addToExtensions(
                ConversionUtils.marshalYAWLFragment("secondary", getObject().getSecondary(), ResourcingSecondaryFactsType.class),
                getConvertedParent());
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

    private void convertDistributionSet(final ResourcingDistributionSetFactsType distributionSet) throws CanoniserException {

        final InitialSet initialSet = distributionSet.getInitialSet();
        final List<String> participantList = initialSet.getParticipant();
        final List<String> roleList = initialSet.getRole();

        if (containsManyResources(initialSet)) {
            // Add reference to Distribution Set
            final ResourceTypeType resourceType = CPF_FACTORY.createResourceTypeType();
            resourceType.setName(DISTRIBUTION_SET_RESOURCE_NAME);
            final DistributionSetType distributionSetExt = CPF_FACTORY.createDistributionSetType();
            resourceType.setDistributionSet(distributionSetExt);

            // Add YAWL extension elements to ANF
            ConversionUtils.addToExtensions(ConversionUtils.marshalYAWLFragment("constraints", distributionSet.getConstraints(), Constraints.class),
                    resourceType);
            ConversionUtils
                    .addToExtensions(ConversionUtils.marshalYAWLFragment("filters", distributionSet.getFilters(), Filters.class), resourceType);

            createResourceReference(resourceType, null);

            for (final String participantId : participantList) {
                final ResourceTypeType resource = createResourceTypeForParticipant(getContext().getParticipantById(participantId));
                final DistributionSetRef ref = CPF_FACTORY.createDistributionSetRef();
                ref.setResourceTypeId(resource.getId());
                distributionSetExt.getResourceTypeRef().add(ref);
            }

            for (final String roleId : roleList) {
                final ResourceTypeType resource = createResourceTypeForRole(getContext().getRoleById(roleId));
                final DistributionSetRef ref = CPF_FACTORY.createDistributionSetRef();
                ref.setResourceTypeId(resource.getId());
                distributionSetExt.getResourceTypeRef().add(ref);
            }

        } else {
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

    private boolean containsManyResources(final InitialSet initialSet) {
        return initialSet.getParticipant().size() + initialSet.getRole().size() > 1;
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
