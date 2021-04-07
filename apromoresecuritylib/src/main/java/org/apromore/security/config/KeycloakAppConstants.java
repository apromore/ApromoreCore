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
package org.apromore.security.config;

public class KeycloakAppConstants {

    public static final String AUTH_HEADER_KEY = "App_Auth"; // Will eventually be "Authorisation"
    public static final String SIGNED_AUTH_HEADER_KEY = "Signed_App_Auth";

    public static final String PRODUCT_APP_CLIENT_ID = "product-app";
    public static final String SPRING_DEMO_DEV_REALM = "springdemo";

    public static final String BEARER_HTTP_AUTH_HEADER_PREFIX = "bearer ";

    public static final String REALM_DEV_ADMIN_USERNAME = "springdemoadmin";
    public static final String REALM_DEV_ADMIN_PASSWORD = "password";

    public static final String KC_DEV_SERVER_URL = "https://keycloak.apromoresso.net:8443/auth";

    public static final String ADMIN_CLI_CLIENT_ID = "admin-cli";
}
