/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.07.16 at 11:52:41 AM AEST 
//


package org.apromore.portal.model;

import java.util.Arrays;

public enum PermissionType {
    PORTAL_LOGIN("d7ff28d6-7cdc-11ec-90d6-0242ac120003","Portal login"),
    USERS_VIEW("dff60714-1d61-4544-8884-0d8b852ba41e","View users"),
    USERS_EDIT("2e884153-feb2-4842-b291-769370c86e44","Edit users"),
    GROUPS_EDIT("d9ade57c-14c7-4e43-87e5-6a9127380b1b","Edit groups"),
    ROLES_EDIT("ea31a607-212f-447e-8c45-78f1e59b1dde","Edit roles"),
    ACCESS_RIGHTS_MANAGE("165962fc-9913-11ec-b909-0242ac120002","Manage access rights"),
    PIPELINE_CREATE("41877a02-722d-43f7-b47b-75c544013f0f", "Create pipeline"),
    PIPELINE_MANAGE("a5e0c805-0dec-469c-8bda-1d279e751bd7", "Manage pipelines"),
    CALENDAR("db2c4517-c9c7-421e-bccf-277baf2fdb72", "Manage calendars"),
    MODEL_CREATE("e673de93-0646-447e-94fe-98b095290bfe", "Create model"),
    MODEL_DISCOVER_EDIT("09c77c0b-6aef-46b2-ad87-e0742092e3ad", "Discover model"),
    MODEL_DISCOVER_VIEW("f404c3e9-5a4d-4d4e-9d6b-0aa2e806c7bc", "View discover model"),
    MODEL_EDIT("df0e4630-2019-41aa-980d-73eb08e612ac", "Edit model"),
    MODEL_VIEW("0159eb6c-226e-4c85-8098-b3538f0be1b8", "View models"),
    FILTER_EDIT("03c2ff79-738d-47cb-9386-0cc745604a75", "Filter log"),
    FILTER_VIEW("c4f70933-dd23-4ccb-bdcd-85681124d34c", "View filter"),
    ANIMATE("bc0253f4-5ae1-44a6-83e2-bbe63f239dd7", "Animate logs"),
    COMPARE_MODELS("86a5eea2-57f8-4ba4-a88c-e007f4b18ab9", "Compare models"),
    CHECK_CONFORMANCE("76d044bd-52b9-4261-bc2b-969656f0bd39", "Check conformance"),
    SIMULATE_MODEL("09e6f182-a2af-448e-a7f6-6d02a0477360", "Simulate model"),
    DASH_EDIT("a1c46230-c5c5-420b-99a2-43be5fbabd3a", "Edit dashboards"),
    DASH_VIEW("9eabfb63-8e39-4e03-803d-c94dbe431059", "View dashboards"),
    MERGE_MODELS("7e1f8cc2-7532-406c-b106-3bf7095047dc", "Merge models"),
    SEARCH_MODELS("7c0f5dc2-7d39-456e-b1cb-139bb030ee98", "Search similar models"),
    PUBLISH_MODELS("607b5dfe-9508-11ec-b909-0242ac120002", "Publish models"),
    UNREGISTERED("", "");

    protected String id;
    protected String name;

    PermissionType (final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get PermissionType by name
     *
     * @param id
     * @param name
     * @return the PermissionType object
     */
    public static PermissionType getPermissionType(final String id, final String name) {
        return Arrays.stream(PermissionType.values())
                .filter(permissionType -> permissionType.id.equals(id) && permissionType.name.equals(name))
                .findFirst()
                .orElse(PermissionType.UNREGISTERED);
    }

    /**
     * Get PermissionType by name
     *
     * @param name
     * @return the PermissionType object
     */
    public static PermissionType getPermissionTypeByName(final String name) {
        return Arrays.stream(PermissionType.values())
                .filter(permissionType -> permissionType.name.equals(name))
                .findFirst()
                .orElse(PermissionType.UNREGISTERED);
    }

    /**
     * Get PermissionType by id
     *
     * @param id
     * @return the PermissionType object
     */
    public static PermissionType getPermissionTypeById(final String id) {
        return Arrays.stream(PermissionType.values())
                .filter(permissionType -> permissionType.id.equals(id))
                .findFirst()
                .orElse(PermissionType.UNREGISTERED);
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

}
