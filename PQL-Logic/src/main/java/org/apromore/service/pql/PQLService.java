/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.pql;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.pql.index.IndexStatus;

/**
 * Created by corno on 2/07/2014.
 */
public interface PQLService {

    void indexAllModels();

    List<String> runAPQLQuery(String queryPQL, List<String> IDs, String userID);
    //List<Detail> getDetails();
    IndexStatus getIndexStatus(String id) throws SQLException;

    /**
     * Is the PQL index present?  If this is false, none of the other methods of this service will function.
     *
     * @return the value of the <code>pql.isIndexingEnabled</code> property from
     *         the <code>site.properties</code> Spring configuration artifact.
     */
    boolean isIndexingEnabled();
}
