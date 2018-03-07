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
package org.apromore.canoniser.yawl.internal.impl.context;

import java.util.HashMap;
import java.util.Map;

import org.yawlfoundation.yawlschema.orgdata.CapabilityType;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;
import org.yawlfoundation.yawlschema.orgdata.ParticipantType;
import org.yawlfoundation.yawlschema.orgdata.PositionType;
import org.yawlfoundation.yawlschema.orgdata.RoleType;

/**
 * Context information about the resource perspective of a CPF -> YAWL conversion.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class CanonicalResourceConversionContext {

    private final OrgDataType yawlOrgData;

    /**
     * Map of all already converted YAWL Roles by their Resource-ID
     */
    private Map<String, RoleType> convertedRoleMap;
    /**
     * Map of all already converted YAWL Participants by their Resource-ID
     */
    private Map<String, ParticipantType> convertedParticipantMap;
    /**
     * Map of all already converted YAWL Positions by their name
     */
    private Map<String, PositionType> convertedPositionMap;
    /**
     * Map of all already converted YAWL Capabilities by their name
     */
    private Map<String, CapabilityType> convertedCapabilityMap;

    public CanonicalResourceConversionContext() {
        yawlOrgData = new org.yawlfoundation.yawlschema.orgdata.ObjectFactory().createOrgDataType();
    }

    public OrgDataType getYawlOrgData() {
        return yawlOrgData;
    }

    /**
     * Gets a YAWL role by their original ID in CPF. Return NULL is Role is not yet converted.
     *
     * @param cpfRoleId of the ResourceType in CPF
     * @return the YAWL role or NULL
     */
    public RoleType getConvertedRole(final String cpfRoleId) {
        initConvertedRoleMap();
        return convertedRoleMap.get(cpfRoleId);
    }


    /**
     * Adds a YAWL role that is already converted by their original ID in CPF.
     *
     * @param cpfRoleId of the ResourceType in CPF
     * @param role the YAWL role
     */
    public void addConvertedRole(final String cpfRoleId, final RoleType role) {
        initConvertedRoleMap();
        convertedRoleMap.put(cpfRoleId, role);
    }

    private void initConvertedRoleMap() {
        if (convertedRoleMap == null) {
            convertedRoleMap = new HashMap<String, RoleType>();
        }
    }

    /**
     * Gets a YAWL participant by the original ID in CPF. Return NULL is participant is not yet converted.
     *
     * @param cpfParticipantId of the ResourceType in CPF
     * @return the YAWL participant or NULL
     */
    public ParticipantType getConvertedParticipant(final String cpfParticipantId) {
        initConvertedParticipantMap();
        return convertedParticipantMap.get(cpfParticipantId);
    }

    /**
     * Adds a YAWL participant that is already converted by their original ID in CPF.
     *
     * @param cpfParticipantId of the ResourceType in CPF
     * @param participant the YAWL participant
     */
    public void addConvertedParticipant(final String cpfParticipantId, final ParticipantType participant) {
        initConvertedParticipantMap();
        convertedParticipantMap.put(cpfParticipantId, participant);
    }

    private void initConvertedParticipantMap() {
        if (convertedParticipantMap == null) {
            convertedParticipantMap = new HashMap<String, ParticipantType>();
        }
    }

    /**
     * Gets a YAWL position by the original name in CPF. Return NULL is position is not yet converted.
     *
     * @param name of the position as in CPF
     * @return the YAWL position or NULL
     */
    public PositionType getConvertedPositionByName(final String name) {
        initConvertedPositionMap();
        return convertedPositionMap.get(name);
    }

    /**
     * Adds a YAWL position that is already converted by the original name in CPF.
     *
     * @param name of the position as in CPF
     * @param position the YAWL position
     */
    public void addConvertedPosition(final String name, final PositionType position) {
        initConvertedPositionMap();
        convertedPositionMap.put(name, position);
    }

    private void initConvertedPositionMap() {
        if (convertedPositionMap == null) {
            convertedPositionMap = new HashMap<String, PositionType>();
        }
    }

    /**
     * Gets a YAWL capability by the original name in CPF. Return NULL is capability is not yet converted.
     *
     * @param name of the capability in CPF
     * @return the YAWL capability or NULL
     */
    public CapabilityType getConvertedCapabilityByName(final String name) {
        initConvertedCapabilityMap();
        return convertedCapabilityMap.get(name);
    }

    /**
     *  Adds a YAWL capability that is already converted by the original name in CPF.
     *
     * @param name of the capability in CPF
     * @param capability the YAWL capability
     */
    public void addConvertedCapability(final String name, final CapabilityType capability) {
        initConvertedCapabilityMap();
        convertedCapabilityMap.put(name, capability);
    }

    private void initConvertedCapabilityMap() {
        if (convertedCapabilityMap == null) {
            convertedCapabilityMap = new HashMap<String, CapabilityType>();
        }
    }

}