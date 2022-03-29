/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.mapper;

import java.util.List;

import org.apromore.portal.model.DomainsType;

// TODO: fix the circular dependency by remove all the Mappers to client

/**
 * Mapper helper class to convert from the DAO Model to the Webservice Model.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class DomainMapper {

    /**
     * Convert from the a list (String) to the WS model (DomainsType).
     *
     * @param domains the list of SearchHistoriesType from the WebService
     * @return the DomainsType ready for transport to the calling system.
     */
    public static DomainsType convertFromDomains(List<String> domains) {
        DomainsType types = new DomainsType();
        for (String domain : domains) {
            types.getDomain().add(domain);
        }
        return types;
    }

}
