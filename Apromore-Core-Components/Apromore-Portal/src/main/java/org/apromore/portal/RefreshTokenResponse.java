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
package org.apromore.portal;

public class RefreshTokenResponse {
    private String authHeader;
    private String signedAuthHeader;
    private boolean wasRefreshed;

    public RefreshTokenResponse() {
    }

    public RefreshTokenResponse(
            final String authHeader,
            final String signedAuthHeader,
            final boolean wasRefreshed) {
        this.authHeader = authHeader;
        this.signedAuthHeader = signedAuthHeader;
        this.wasRefreshed = wasRefreshed;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public String getSignedAuthHeader() {
        return signedAuthHeader;
    }

    public boolean isWasRefreshed() {
        return wasRefreshed;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

    public void setSignedAuthHeader(String signedAuthHeader) {
        this.signedAuthHeader = signedAuthHeader;
    }

    public void setWasRefreshed(boolean wasRefreshed) {
        this.wasRefreshed = wasRefreshed;
    }

    @Override
    public String toString() {
        return "RefreshTokenResponse{" +
                "authHeader='" + authHeader + '\'' +
                ", signedAuthHeader='" + signedAuthHeader + '\'' +
                ", wasRefreshed=" + wasRefreshed +
                '}';
    }
}