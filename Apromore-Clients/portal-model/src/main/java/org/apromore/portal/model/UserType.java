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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserType {
    protected List<RoleType> roles;
    protected List<PermissionType> permissions;
    protected List<SearchHistoriesType> searchHistories;
    protected MembershipType membership;
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String organization;
    protected String role;
    protected String country;
    protected String phone;
    protected String subscription;
    protected String lastActivityDate;
    protected String username;

    public UserType() {
    }


    /**
     * Constructs a basic UserType based on four specified parameters.
     *
     * @param username Username for user.
     * @param email Email address for user.
     * @param givenName Given name for user.
     * @param familyName Family name for user.
     */
    public UserType(final String username,
                    final String email,
                    final String givenName,
                    final String familyName) {
        // Create the user record
        final MembershipType membershipType = new MembershipType();
        membershipType.setEmail(email);
        membershipType.setFailedLogins(0);
        membershipType.setFailedAnswers(0);

        this.setFirstName(givenName);
        this.setLastName(familyName);
        this.setUsername(username);
        this.setMembership(membershipType);
    }

    /**
     * Gets the value of the roles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RoleType }
     * 
     * 
     */
    public List<RoleType> getRoles() {
        if (roles == null) {
            roles = new ArrayList<RoleType>();
        }
        return this.roles;
    }

    /**
     * Gets the value of the permissions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the permissions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPermissions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PermissionType }
     * 
     * 
     */
    public List<PermissionType> getPermissions() {
        if (permissions == null) {
            permissions = new ArrayList<PermissionType>();
        }
        return this.permissions;
    }

    /**
     * Gets the value of the searchHistories property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the searchHistories property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSearchHistories().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SearchHistoriesType }
     * 
     * 
     */
    public List<SearchHistoriesType> getSearchHistories() {
        if (searchHistories == null) {
            searchHistories = new ArrayList<SearchHistoriesType>();
        }
        return this.searchHistories;
    }

    /**
     * @param permissionName The name of a permission.
     * @return true if the user has a permission with any of the given names.
     */
    public boolean hasAnyPermission(String... permissionName) {
        return getPermissions().stream().anyMatch(p -> Arrays.asList(permissionName).contains(p.getName()));
    }

    /**
     * Gets the value of the membership property.
     * 
     * @return
     *     possible object is
     *     {@link MembershipType }
     *     
     */
    public MembershipType getMembership() {
        return membership;
    }

    /**
     * Sets the value of the membership property.
     * 
     * @param value
     *     allowed object is
     *     {@link MembershipType }
     *     
     */
    public void setMembership(MembershipType value) {
        this.membership = value;
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
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the organization property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * Sets the value of the organization property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganization(String value) {
        this.organization = value;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the subscription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * Sets the value of the subscription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscription(String value) {
        this.subscription = value;
    }

    /**
     * Gets the value of the lastActivityDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastActivityDate() {
        return lastActivityDate;
    }

    /**
     * Sets the value of the lastActivityDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastActivityDate(String value) {
        this.lastActivityDate = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

}
