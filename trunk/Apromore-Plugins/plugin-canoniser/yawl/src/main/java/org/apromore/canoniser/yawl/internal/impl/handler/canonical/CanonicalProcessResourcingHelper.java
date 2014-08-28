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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import java.util.List;

import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalResourceConversionContext;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.NonhumanType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TypeAttribute;
import org.yawlfoundation.yawlschema.orgdata.CapabilityRef;
import org.yawlfoundation.yawlschema.orgdata.CapabilityType;
import org.yawlfoundation.yawlschema.orgdata.CategoryType;
import org.yawlfoundation.yawlschema.orgdata.NonHumanResourceType;
import org.yawlfoundation.yawlschema.orgdata.ObjectFactory;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;
import org.yawlfoundation.yawlschema.orgdata.ParticipantType;
import org.yawlfoundation.yawlschema.orgdata.PositionRef;
import org.yawlfoundation.yawlschema.orgdata.PositionType;
import org.yawlfoundation.yawlschema.orgdata.RoleRef;
import org.yawlfoundation.yawlschema.orgdata.RoleType;

/**
 * Helps CanonicalProcessHandler to convert Resources
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class CanonicalProcessResourcingHelper {

    private static final ObjectFactory YAWL_ORG_FACTORY = new ObjectFactory();

    private final CanonicalConversionContext context;

    /**
     * Creates the helper class to convert resources of a CanonicalProcessType
     *
     * @param context
     */
    public CanonicalProcessResourcingHelper(final CanonicalConversionContext context) {
        super();
        this.context = context;
    }

    private CanonicalResourceConversionContext getResourceContext() {
        return context.getResourceContext();
    }

    private CanonicalConversionContext getContext() {
        return context;
    }

    /**
     * Convert the organisational Data of a Canonical Process
     *
     * @param resourceList
     *            of the CPF process
     */
    public void convertOrganisationalData(final List<ResourceTypeType> resourceList) {
        for (ResourceTypeType r : resourceList) {
            convertSingleResource(r);
        }
    }

    private String convertSingleResource(final ResourceTypeType r) {
        if (r instanceof NonhumanType && ((NonhumanType) r).getType() != null) {
            return convertSingleNonHuman((NonhumanType) r);
        } else if (r instanceof HumanType && ((HumanType) r).getType() != null) {
            return convertSingleHuman((HumanType) r);
        } else {
            // Assume it is a Role
            return convertRole(r);
        }
    }

    private String convertSingleHuman(final HumanType humanType) {
        switch (humanType.getType()) {
        case DEPARTMENT:
        case ORGANISATION:
        case GROUP:
        case TEAM:
        case UNIT:
            // TODO what to do if Task is assigned directly to OrgGroup
            return ""; // return convertOrgGroup(humanType);
        case ROLE:
            return convertRole(humanType);
        case PARTICIPANT:
            return convertParticipant(humanType);
        default:
            // Assume it is a Role
            return convertRole(humanType);
        }
    }

    // private String convertOrgGroup(final HumanType humanType) {
    // //TODO look if already converted
    // OrgDataType orgData = getContext().getYawlOrgData();
    // if (orgData.getOrggroups() != null) {
    // orgData.setOrggroups(YAWL_ORG_FACTORY.createOrggroupsType());
    // }
    // OrggroupType resource = YAWL_ORG_FACTORY.createOrggroupType();
    // resource.setId(generateResourceUUID(humanType));
    // resource.setName(humanType.getName());
    // orgData.getOrggroups().getOrggroup().add(resource);
    // for (String sId: humanType.getSpecializationIds()) {
    // ResourceTypeType specialization = getContext().getResourceTypeById(sId);
    // convertSingleResource(specialization);
    // }
    // return resource.getId();
    // }

    private String convertSingleNonHuman(final NonhumanType r) {
        switch (r.getType()) {
        case EQUIPMENT:
            return convertSecondaryResource(r);
        case SOFTWARE_SYSTEM:
            return null; // Ignored for YAWL
        default:
            // Ignore
            return null;
        }
    }

    private String convertSecondaryResource(final NonhumanType r) {
        OrgDataType orgData = getResourceContext().getYawlOrgData();
        if (orgData.getNonhumanresources() == null) {
            orgData.setNonhumanresources(YAWL_ORG_FACTORY.createNonHumanResourcesType());
        }
        NonHumanResourceType resource = YAWL_ORG_FACTORY.createNonHumanResourceType();
        resource.setId(generateResourceUUID(r));
        resource.setName(r.getName());
        resource.setNotes("");
        resource.setDescription("");
        //TODO categories
        resource.setCategory(new CategoryType());
        resource.setSubcategory("None");
        orgData.getNonhumanresources().getNonhumanresource().add(resource);
        return resource.getId();
    }

    private String generateResourceUUID(final ResourceTypeType r) {
        return getContext().getUuidGenerator().getUUID(r.getOriginalID() != null ? r.getOriginalID() : r.getId());
    }

    private String convertParticipant(final HumanType humanType) {
        if (getResourceContext().getConvertedParticipant(humanType.getId()) == null) {
            OrgDataType orgData = getResourceContext().getYawlOrgData();
            if (orgData.getParticipants() == null) {
                orgData.setParticipants(YAWL_ORG_FACTORY.createParticipantsType());
            }
            ParticipantType p = YAWL_ORG_FACTORY.createParticipantType();
            p.setId(generateResourceUUID(humanType));
            p.setUserid(humanType.getName());
            p.setFirstname(humanType.getName());
            p.setDescription("");
            p.setLastname("");
            p.setDescription("");
            p.setNotes("");
            p.setPrivileges(0);
            p.setRoles(new RoleRef());
            p.setPositions(convertPositions(humanType));
            p.setCapabilities(convertCapabilities(humanType));
            p.setPassword(humanType.getName());
            orgData.getParticipants().getParticipant().add(p);
            getResourceContext().addConvertedParticipant(humanType.getId(), p);
            return p.getId();
        } else {
            return getResourceContext().getConvertedParticipant(humanType.getId()).getId();
        }
    }

    private CapabilityRef convertCapabilities(final HumanType humanType) {
        CapabilityRef capabilityRef = YAWL_ORG_FACTORY.createCapabilityRef();
        for (TypeAttribute attr : humanType.getAttribute()) {
            if ("Capability".equals(attr.getName())) {
                CapabilityType c = convertSingleCapability(attr);
                capabilityRef.getCapability().add(YAWL_ORG_FACTORY.createCapabilityRefCapability(c));
            }
        }
        return capabilityRef;
    }

    private void initCapabilities() {
        OrgDataType orgData = getResourceContext().getYawlOrgData();
        if (orgData.getCapabilities() == null) {
            orgData.setCapabilities(YAWL_ORG_FACTORY.createCapabilitiesType());
        }
    }

    private CapabilityType convertSingleCapability(final TypeAttribute attr) {
        CapabilityType convertedCapability = getResourceContext().getConvertedCapabilityByName(attr.getValue());
        if (convertedCapability != null) {
            return convertedCapability;
        } else {
            CapabilityType c = YAWL_ORG_FACTORY.createCapabilityType();
            c.setId(getContext().getUuidGenerator().getUUID(null));
            c.setName(attr.getValue());
            c.setDescription("");
            c.setNotes("");
            addCapability(attr.getValue(), c);
            return c;
        }
    }

    private void addCapability(final String name, final CapabilityType c) {
        initCapabilities();
        getResourceContext().getYawlOrgData().getCapabilities().getCapability().add(c);
        getResourceContext().addConvertedCapability(name, c);
    }

    private PositionRef convertPositions(final HumanType humanType) {
        PositionRef positionRef = YAWL_ORG_FACTORY.createPositionRef();
        for (TypeAttribute attr : humanType.getAttribute()) {
            if ("Position".equals(attr.getName())) {
                PositionType p = convertSinglePosition(attr);
                positionRef.getPosition().add(YAWL_ORG_FACTORY.createPositionRefPosition(p));
            }
        }
        return positionRef;
    }

    private void initPositions() {
        OrgDataType orgData = getResourceContext().getYawlOrgData();
        if (orgData.getPositions() == null) {
            orgData.setPositions(YAWL_ORG_FACTORY.createPositionsType());
        }
    }

    private PositionType convertSinglePosition(final TypeAttribute attr) {
        PositionType convertedPosition = getResourceContext().getConvertedPositionByName(attr.getValue());
        if (convertedPosition != null) {
            return convertedPosition;
        } else {
            PositionType p = YAWL_ORG_FACTORY.createPositionType();
            p.setId(getContext().getUuidGenerator().getUUID(null));
            p.setPositionid(getContext().getUuidGenerator().getUUID(null));
            p.setTitle(attr.getValue());
            p.setDescription("");
            p.setNotes("");
            addPosition(attr.getValue(), p);
            return p;
        }
    }

    private void addPosition(final String name, final PositionType p) {
        initPositions();
        getResourceContext().getYawlOrgData().getPositions().getPosition().add(p);
        getResourceContext().addConvertedPosition(name, p);
    }

    private String convertRole(final ResourceTypeType role) {
        if (getResourceContext().getConvertedRole(role.getId()) == null) {
            OrgDataType orgData = getResourceContext().getYawlOrgData();
            if (orgData.getRoles() == null) {
                orgData.setRoles(YAWL_ORG_FACTORY.createRolesType());
            }
            RoleType r = YAWL_ORG_FACTORY.createRoleType();
            r.setId(generateResourceUUID(role));
            r.setName(role.getName());
            r.setDescription("");
            r.setNotes("");
            getResourceContext().addConvertedRole(role.getId(), r);
            orgData.getRoles().getRole().add(r);
            for (String sId : role.getSpecializationIds()) {
                ResourceTypeType specialization = getContext().getResourceTypeById(sId);
                convertSingleResource(specialization);
            }
            return r.getId();
        } else {
            return getResourceContext().getConvertedRole(role.getId()).getId();
        }

    }

}
