/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.JAXBElement;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLConversionHandler;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.canoniser.yawl.internal.utils.YAWLConstants;
import org.apromore.cpf.AllocationStrategyEnum;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.HumanTypeEnum;
import org.apromore.cpf.NonhumanType;
import org.apromore.cpf.NonhumanTypeEnum;
import org.apromore.cpf.ResourceDataFilterExpressionType;
import org.apromore.cpf.ResourceRuntimeFilterExpressionType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
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
import org.yawlfoundation.yawlschema.ResourcingOfferFactsType.FamiliarParticipant;
import org.yawlfoundation.yawlschema.ResourcingParamFactsType.Param;
import org.yawlfoundation.yawlschema.ResourcingResourceType;
import org.yawlfoundation.yawlschema.ResourcingSecondaryFactsType;
import org.yawlfoundation.yawlschema.ResourcingSelectorFactsType;
import org.yawlfoundation.yawlschema.orgdata.CapabilityType;
import org.yawlfoundation.yawlschema.orgdata.NonHumanResourceType;
import org.yawlfoundation.yawlschema.orgdata.ParticipantType;
import org.yawlfoundation.yawlschema.orgdata.PositionType;
import org.yawlfoundation.yawlschema.orgdata.RoleType;

/**
 * Converting YAWL resources to CPF
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class ResourceingHandler extends YAWLConversionHandler<ResourcingFactsType, TaskType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceingHandler.class);

    private static final String AND_DESCRIPTION = " and ";

    private static final Object AND_EXPRESSION = " AND ";

    public static final String SECONDARY_QUALIFIER = "Secondary";

    public static final String PRIMARY_QUALIFIER = "Primary";

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

            LOGGER.warn("Task {} is offered by user, can not convert to CPF resources!", task.getId());

            // Distribution of work will be handled by User at Runtime, there is no way of capturing this in CPF
            if (getObject().getOffer() == null) {
                ExtensionUtils
                        .addToExtensions(ExtensionUtils.marshalYAWLFragment(ExtensionUtils.OFFER, getObject(), ResourcingFactsType.class), task);
            }

        } else {

            convertDistributionSet(getObject());
            convertAllocation(getObject().getAllocate(), task);

            final ResourcingOfferFactsType offer = getObject().getOffer();
            if (offer != null) {
                ResourcingDistributionSetFactsType distributionSet = offer.getDistributionSet();
                if (distributionSet != null) {
                    if (hasFilter(offer.getDistributionSet())) {
                        convertFilter(offer.getDistributionSet(), task);
                    }
                    if (hasConstraints(offer)) {
                        convertConstraints(offer.getDistributionSet().getConstraints(), offer.getFamiliarParticipant(), task);
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

    private boolean hasConstraints(final ResourcingOfferFactsType offer) {
        return offer.getDistributionSet().getConstraints() != null || offer.getFamiliarParticipant() != null;
    }

    private boolean hasFilter(final ResourcingDistributionSetFactsType distributionSet) {
        return distributionSet.getFilters() != null || distributionSet.getInitialSet().getParam() != null;
    }

    private void convertAllocation(final ResourcingAllocateFactsType allocate, final TaskType task) throws CanoniserException {

        if (isSystemAllocated(allocate)) {

            ResourcingSelectorFactsType allocator = allocate.getAllocator();
            if (allocator != null) {
                task.setAllocationStrategy(translateAllocatorName(allocator));
                if (task.getAllocationStrategy().equals(AllocationStrategyEnum.OTHER)) {
                    ExtensionUtils.addToExtensions(ExtensionUtils.marshalYAWLFragment("allocator", allocator, ResourcingSelectorFactsType.class),
                            task);
                }
            }

        } else {
            // Allocation of work will be handled by User at Runtime, there is no way of capturing this in CPF
            if (allocate != null) {
                ExtensionUtils.addToExtensions(
                        ExtensionUtils.marshalYAWLFragment(ExtensionUtils.ALLOCATE, getObject().getAllocate(), ResourcingAllocateFactsType.class),
                        task);
            }
        }
    }

    private AllocationStrategyEnum translateAllocatorName(final ResourcingSelectorFactsType allocator) {
        if (allocator.getName().equals(YAWLConstants.YAWL_ALLOCATOR_SHORTEST_QUEUE)) {
            return AllocationStrategyEnum.SHORTEST_QUEUE;
        } else if (allocator.getName().equals(YAWLConstants.YAWL_ALLOCATOR_ROUND_ROBIN_BY_TIME)) {
            return AllocationStrategyEnum.ROUND_ROBIN_BY_TIME;
        } else if (allocator.getName().equals(YAWLConstants.YAWL_ALLOCATOR_ROUND_ROBIN_BY_EXPERIENCE)) {
            return AllocationStrategyEnum.ROUND_ROBIN_BY_EXPERIENCE;
        } else if (allocator.getName().equals(YAWLConstants.YAWL_ALLOCATOR_ROUND_ROBIN_BY_LEAST_FREQUENCY)) {
            return AllocationStrategyEnum.ROUND_ROBIN_BY_FREQUENCY;
        } else if (allocator.getName().equals(YAWLConstants.YAWL_ALLOCATOR_RANDOM)) {
            return AllocationStrategyEnum.RANDOM;
        } else {
            return AllocationStrategyEnum.OTHER;
        }
    }

    private void convertConstraints(final Constraints constraints, final FamiliarParticipant familiarParticipant, final TaskType task) {
        ResourceRuntimeFilterExpressionType filterRuntimeExpr = CPF_FACTORY.createResourceRuntimeFilterExpressionType();
        filterRuntimeExpr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_APROMORE_RESOURCE_RUNTIME);

        StringBuilder exprBuilder = new StringBuilder();

        if (constraints != null) {
            ListIterator<ResourcingSelectorFactsType> constraintIter = constraints.getConstraint().listIterator();
            while (constraintIter.hasNext()) {
                ResourcingSelectorFactsType c = constraintIter.next();
                if (c.getName().equals("SeparationOfDuties")) {
                    if (c.getParams().getParam().isEmpty()) {
                        String taskID = c.getParams().getParam().get(0).getValue();
                        exprBuilder.append("separationOfDuties(" + generateUUID(CONTROLFLOW_ID_PREFIX, taskID) + ")");
                    }
                } else if (c.getName().equals("PiledExecution")) {
                    exprBuilder.append("piledExecution");
                }

                if (constraintIter.hasNext()) {
                    exprBuilder.append(AND_EXPRESSION);
                }
            }
        }

        if (familiarParticipant != null && exprBuilder.length() > 0) {
            exprBuilder.append(AND_EXPRESSION);
            exprBuilder.append("familiarParticipant(" + generateUUID(CONTROLFLOW_ID_PREFIX, familiarParticipant.getTaskID()) + ")");
        }

        filterRuntimeExpr.setExpression(exprBuilder.toString());
        task.setFilterByRuntimeExpr(filterRuntimeExpr);
    }

    private static final String FILTER_POSTFIX = "]";
    private static final String FILTER_PREFIX = "//ResourceType[";

    private void convertFilter(final ResourcingDistributionSetFactsType distributionSet, final TaskType task)
            throws CanoniserException {
        ResourceDataFilterExpressionType filterDataExpr = CPF_FACTORY.createResourceDataFilterExpressionType();
        filterDataExpr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XPATH);

        StringBuilder exprBuilder = new StringBuilder(FILTER_PREFIX);
        StringBuilder descrBuilder = new StringBuilder();

        Filters filters = distributionSet.getFilters();
        List<InitialSet.Param> dataParams = distributionSet.getInitialSet().getParam();

        if (dataParams != null) {
            appendAllDataParamExpr(exprBuilder, descrBuilder, dataParams);
        }

        if (filters != null) {
            appendAllFilterExpr(task, exprBuilder, descrBuilder, filters);
        }
        exprBuilder.append(FILTER_POSTFIX);

        filterDataExpr.setDescription(descrBuilder.toString());
        filterDataExpr.setExpression(exprBuilder.toString());

        if (!(filterDataExpr.getDescription().isEmpty() && (filterDataExpr.getExpression().length() == FILTER_PREFIX.length() + FILTER_POSTFIX.length()))) {
            task.setFilterByDataExpr(filterDataExpr);
        }
    }

    private void appendAllDataParamExpr(final StringBuilder exprBuilder, final StringBuilder descrBuilder,
            final List<org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType.InitialSet.Param> dataParams) {
        for (org.yawlfoundation.yawlschema.ResourcingDistributionSetFactsType.InitialSet.Param param : dataParams) {
            exprBuilder.append(buildDataParamXPath(param.getName(), param.getRefers()));
            descrBuilder.append(buildDataParamDescription(param.getName(), param.getRefers()));
        }
    }

    private void appendAllFilterExpr(final TaskType task, final StringBuilder exprBuilder, final StringBuilder descrBuilder, final Filters filters) throws CanoniserException {
        ListIterator<ResourcingSelectorFactsType> filterIter = filters.getFilter().listIterator();
        while (filterIter.hasNext()) {
            ResourcingSelectorFactsType filter = filterIter.next();

            if (filter.getName().equals("CapabilityFilter")) {
                convertCapabilityFilter(exprBuilder, descrBuilder, filter);
            } else if (filter.getName().equals("OrgFilter")) {
                convertOrgFilter(exprBuilder, descrBuilder, filter);
            } else {
                // Unknown YAWL filter add to extension
                ExtensionUtils.addToExtensions(
                        ExtensionUtils.marshalYAWLFragment(ExtensionUtils.FILTER, filter, ResourcingSelectorFactsType.class), task);
            }

            if (filterIter.hasNext()) {
                descrBuilder.append(AND_DESCRIPTION);
                exprBuilder.append(AND_EXPRESSION);
            }
        }
    }

    private void convertOrgFilter(final StringBuilder exprBuilder, final StringBuilder descrBuilder, final ResourcingSelectorFactsType filter) {
        if (!filter.getParams().getParam().isEmpty()) {
            ListIterator<Param> paramIter = filter.getParams().getParam().listIterator();
            while (paramIter.hasNext()) {
                Param param = paramIter.next();
                descrBuilder.append(buildOrgFilterDescription(param));
                exprBuilder.append(buildParamXPath(param));

                if (paramIter.hasNext()) {
                    descrBuilder.append(AND_DESCRIPTION);
                    exprBuilder.append(AND_EXPRESSION);
                }
            }
        }
    }

    private void convertCapabilityFilter(final StringBuilder exprBuilder, final StringBuilder descrBuilder, final ResourcingSelectorFactsType filter) {
        if (!filter.getParams().getParam().isEmpty()) {
            Param param = filter.getParams().getParam().get(0);
            descrBuilder.append(buildCapabilityDescription(param));
            exprBuilder.append(buildParamXPath(param));
        }
    }

    private String buildDataParamXPath(final String name, final ResourcingResourceType refers) {
        if (refers.equals(ResourcingResourceType.PARTICIPANT)) {
            return "type/text()='Participant'"+AND_EXPRESSION+"name/text()='cpf:getObjectValue(" + name + ")'";
        } else {
            return "type/text()='Role'"+AND_EXPRESSION+"name/text()='cpf:getObjectValue(" + name + ")'";
        }
    }

    private String buildDataParamDescription(final String name, final ResourcingResourceType refers) {
        return "of type " + refers.toString() + " choose by value of Object " + name;
    }

    private String buildOrgFilterDescription(final Param param) {
        return "in organisational group '" + param.getValue() + "'";
    }

    private String buildCapabilityDescription(final Param param) {
        return "with capability '" + param.getValue() + "'";
    }

    private String buildParamXPath(final Param param) {
        return "attribute[@name='" + param.getKey() + "'"+AND_EXPRESSION+"@value='" + param.getValue() + "']";
    }

    private List<HumanType> convertDistributionSet(final ResourcingFactsType resourcing) {

        List<HumanType> convertedDistributionSet = new ArrayList<HumanType>();

        if (resourcing.getOffer() != null && resourcing.getOffer().getDistributionSet() != null
                && resourcing.getOffer().getDistributionSet().getInitialSet() != null) {
            InitialSet initialSet = resourcing.getOffer().getDistributionSet().getInitialSet();

            List<String> participantList = initialSet.getParticipant();
            List<String> roleList = initialSet.getRole();

            if (!hasDataFilter(initialSet)) {
                // No resource will be in our distribution set
                for (final String participantId : participantList) {
                    ParticipantType part = getContext().getParticipantById(participantId);
                    if (part != null) {
                        final HumanType resource = createResourceTypeForParticipant(part);
                        createResourceReference(resource, PRIMARY_QUALIFIER);
                        convertedDistributionSet.add(resource);
                    }
                }

                for (final String roleId : roleList) {
                    RoleType role = getContext().getRoleById(roleId);
                    if (role != null) {
                        final HumanType resource = createResourceTypeForRole(role);
                        createResourceReference(resource, PRIMARY_QUALIFIER);
                        convertedDistributionSet.add(resource);
                    }
                }
            }
        }

        return convertedDistributionSet;
    }

    private boolean hasDataFilter(final InitialSet initialSet) {
        return !initialSet.getParam().isEmpty();
    }

    private void convertSecondaryResources(final ResourcingSecondaryFactsType secondaryResources) {

        for (final String participantId : secondaryResources.getParticipant()) {
            ParticipantType part = getContext().getParticipantById(participantId);
            if (part != null) {
                final ResourceTypeType resource = createResourceTypeForParticipant(part);
                createResourceReference(resource, SECONDARY_QUALIFIER);
            }
        }

        for (final String roleId : secondaryResources.getRole()) {
            RoleType role = getContext().getRoleById(roleId);
            if (role != null) {
                final ResourceTypeType resource = createResourceTypeForRole(role);
                createResourceReference(resource, SECONDARY_QUALIFIER);
            }
        }

        for (final String nonHumanId: secondaryResources.getNonHumanResource()) {
            NonHumanResourceType nonHumanResource = getContext().getNonHumanById(nonHumanId);
            if (nonHumanResource != null) {
                final ResourceTypeType resource = createResourceTypeForNonHuman(nonHumanResource);
                createResourceReference(resource, SECONDARY_QUALIFIER);
            }
        }

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
        ResourceTypeType resource = getContext().getGeneratedResourceType(role.getId());
        if (!(resource instanceof HumanType)) {
            // Create a new ResourceType only for this Role
            HumanType roleResource = CPF_FACTORY.createHumanType();
            roleResource.setType(HumanTypeEnum.ROLE);
            roleResource.setId(generateUUID(RESOURCE_ID_PREFIX, role.getId()));
            roleResource.setOriginalID(role.getId());
            roleResource.setName(role.getName());
            // Add new Resource as generated Resource
            getContext().setGeneratedResourceType(role.getId(), roleResource);
            // Add the ResourceType to the CanonicalProcess
            getContext().getCanonicalResult().getResourceType().add(roleResource);

            LOGGER.debug("Converted YAWL role {} to CPF HumanType {}", role.getName(), ConversionUtils.toString(roleResource));

            if (role.getBelongsToID() != null) {
                // Recursion is safe here, as we're keeping track of already converted Roles
                final ResourceTypeType parentRole = createResourceTypeForRole(role.getBelongsToID());
                parentRole.getSpecializationIds().add(roleResource.getId());
            }

            for (ParticipantType p : getContext().getOrgDataType().getParticipants().getParticipant()) {
                if (hasRole(role, p)) {
                    createResourceTypeForParticipant(p);
                }
            }

            return roleResource;
        }
        return (HumanType) resource;
    }

    private boolean hasRole(final RoleType role, final ParticipantType p) {
        for (JAXBElement<Object> r : p.getRoles().getRole()) {
            if (r.getValue() instanceof RoleType) {
                RoleType pRole = (RoleType) r.getValue();
                if (pRole.getId().equals(role.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates a HumanType resources for a YAWL participant
     *
     * @param participant
     *            YAWL participant
     * @return HumanType
     */
    private HumanType createResourceTypeForParticipant(final ParticipantType participant) {
        ResourceTypeType resource = getContext().getGeneratedResourceType(participant.getId());
        if (!(resource instanceof HumanType)) {
            // Create a new ResourceType only for this Role
            HumanType participantResource = CPF_FACTORY.createHumanType();
            participantResource.setType(HumanTypeEnum.PARTICIPANT);
            participantResource.setId(generateUUID(RESOURCE_ID_PREFIX, participant.getId()));
            participantResource.setOriginalID(participant.getId());
            participantResource.setName(participant.getFirstname() + " " + participant.getLastname());
            convertCapabilities(participantResource, participant);
            convertPositions(participantResource, participant);
            // Add new Resource as generated Resource
            getContext().setGeneratedResourceType(participant.getId(), participantResource);
            // Add the ResourceType to the CanonicalProcess
            getContext().getCanonicalResult().getResourceType().add(participantResource);

            LOGGER.debug("Converted YAWL participant {} to CPF HumanType {}", participant.getFirstname(), ConversionUtils.toString(participantResource));

            if (participant.getRoles() != null) {
                // Add my Roles
                for (final JAXBElement<Object> role : participant.getRoles().getRole()) {
                    if (role.getValue() instanceof RoleType) {
                        final ResourceTypeType parentRole = createResourceTypeForRole((RoleType) role.getValue());
                        parentRole.getSpecializationIds().add(participantResource.getId());
                    } else {
                        LOGGER.warn("Wrong type of JAXBElement " + role.toString() + " expected RoleType");
                    }

                }
            }

            return participantResource;
        }
        return (HumanType) resource;
    }


    private NonhumanType createResourceTypeForNonHuman(final NonHumanResourceType yawlResource) {
        ResourceTypeType resource = getContext().getGeneratedResourceType(yawlResource.getId());
        if (!(resource instanceof NonhumanType)) {
            // Create a new ResourceType only for this Role
            NonhumanType nonHumanResource = CPF_FACTORY.createNonhumanType();
            nonHumanResource.setType(NonhumanTypeEnum.EQUIPMENT);
            nonHumanResource.setId(generateUUID(RESOURCE_ID_PREFIX, yawlResource.getId()));
            nonHumanResource.setOriginalID(yawlResource.getId());
            nonHumanResource.setName(yawlResource.getName());
            // Add new Resource as generated Resource
            getContext().setGeneratedResourceType(yawlResource.getId(), nonHumanResource);
            // Add the ResourceType to the CanonicalProcess
            getContext().getCanonicalResult().getResourceType().add(nonHumanResource);

            LOGGER.debug("Converted YAWL secondary resource {} to CPF NonhumanType {}", yawlResource.getName(), ConversionUtils.toString(nonHumanResource));
            return nonHumanResource;
        }
        return (NonhumanType) resource;
    }


    private void convertPositions(final HumanType canonicalResource, final ParticipantType participant) {
        for (JAXBElement<Object> element : participant.getPositions().getPosition()) {
            if (element.getValue() instanceof PositionType) {
                PositionType p = (PositionType) element.getValue();
                TypeAttribute attr = CPF_FACTORY.createTypeAttribute();
                attr.setName("Position");
                attr.setValue(p.getTitle());
                canonicalResource.getAttribute().add(attr);
            }
        }
    }

    private void convertCapabilities(final HumanType canonicalResource, final ParticipantType participant) {
        for (JAXBElement<Object> element : participant.getCapabilities().getCapability()) {
            if (element.getValue() instanceof CapabilityType) {
                CapabilityType c = (CapabilityType) element.getValue();
                TypeAttribute attr = CPF_FACTORY.createTypeAttribute();
                attr.setName("Capability");
                attr.setValue(c.getName());
                canonicalResource.getAttribute().add(attr);
            }
        }
    }

}
