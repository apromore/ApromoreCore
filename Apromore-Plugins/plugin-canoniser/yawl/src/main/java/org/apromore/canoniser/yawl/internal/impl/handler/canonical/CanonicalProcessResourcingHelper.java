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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import java.util.List;

import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.NonhumanType;
import org.apromore.cpf.ResourceTypeType;
import org.yawlfoundation.yawlschema.orgdata.CapabilityRef;
import org.yawlfoundation.yawlschema.orgdata.ObjectFactory;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;
import org.yawlfoundation.yawlschema.orgdata.ParticipantType;
import org.yawlfoundation.yawlschema.orgdata.PositionRef;
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

    public CanonicalProcessResourcingHelper(final CanonicalConversionContext context) {
        super();
        this.context = context;
    }

    public void convertOrganisationalData(final List<ResourceTypeType> resourceList) {
        for (ResourceTypeType r : resourceList) {
            convertSingleResource(r);
        }
    }

    private String convertSingleResource(final ResourceTypeType r) {
        if (r instanceof NonhumanType && ((NonhumanType)r).getType() != null) {
            switch (((NonhumanType) r).getType()) {
            case EQUIPMENT:
                return "test";
            case SOFTWARE_SYSTEM:
                return "test";
            }
        } else if (r instanceof HumanType && ((HumanType)r).getType() != null) {
            HumanType humanType = (HumanType) r;
            switch (humanType.getType()) {
            case DEPARTMENT:
            case ORGANISATION:
            case GROUP:
            case TEAM:
            case UNIT:
                return "test";
            case ROLE:
                return convertRole(humanType);
            case PARTICIPANT:
                return convertParticipant(humanType);
            }
        }
        // Assume it is a Role
        return convertRole(r);
    }

    private String convertParticipant(final HumanType humanType) {
        if (getContext().getConvertedParticipant(humanType.getId()) == null) {
            OrgDataType orgData = getContext().getYawlOrgData();
            if (orgData.getParticipants() == null) {
                orgData.setParticipants(YAWL_ORG_FACTORY.createParticipantsType());
            }
            ParticipantType p = YAWL_ORG_FACTORY.createParticipantType();
            p.setId(getContext().getUuidGenerator().getUUID(humanType.getOriginalID() != null ? humanType.getOriginalID() : humanType.getId()));
            p.setUserid(humanType.getName());
            p.setFirstname(humanType.getName());
            p.setDescription("");
            p.setLastname("");
            p.setDescription("");
            p.setNotes("");
            p.setPrivileges(0);
            p.setRoles(new RoleRef());
            p.setPositions(new PositionRef());
            p.setCapabilities(new CapabilityRef());
            p.setPassword(humanType.getName());
            orgData.getParticipants().getParticipant().add(p);
            getContext().addConvertedParticipant(humanType.getId(), p);
            return p.getId();
        } else {
            return getContext().getConvertedParticipant(humanType.getId()).getId();
        }
    }

    private String convertRole(final ResourceTypeType resource) {
        if (getContext().getConvertedRole(resource.getId()) == null) {
            OrgDataType orgData = getContext().getYawlOrgData();
            if (orgData.getRoles() == null) {
                orgData.setRoles(YAWL_ORG_FACTORY.createRolesType());
            }
            RoleType r = YAWL_ORG_FACTORY.createRoleType();
            r.setId(getContext().getUuidGenerator().getUUID(resource.getOriginalID() != null ? resource.getOriginalID() : resource.getId()));
            r.setName(resource.getName());
            r.setDescription("");
            r.setNotes("");
            getContext().addConvertedRole(resource.getId(), r);
            orgData.getRoles().getRole().add(r);
            for (String sId: resource.getSpecializationIds()) {
                ResourceTypeType specialization = getContext().getResourceTypeById(sId);
                String yawlId = convertSingleResource(specialization);
            }
            return r.getId();
        } else {
            return getContext().getConvertedRole(resource.getId()).getId();
        }

    }

    public CanonicalConversionContext getContext() {
        return context;
    }

}
