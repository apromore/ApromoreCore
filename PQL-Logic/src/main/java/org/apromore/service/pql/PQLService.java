/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.service.pql;

import java.sql.SQLException;
import java.util.List;

import org.pql.index.IndexStatus;

import org.apromore.model.Detail;
import org.apromore.model.SummariesType;

/**
 * Created by corno on 2/07/2014.
 */
public interface PQLService {

    /**
     * @param pql  a grammatical PQL query
     * @throws QueryParsingException if <var>pql</var> isn't well-formed
     */
    SummariesType query(String pql) throws QueryException;

    /*
     * @param queryPQL  a grammatical PQL query
     * @param IDs  the default
     * @param userID  the ostensible user name making the request
     * @return the external IDs of the indexed process models which satisfy the query
     */
    List<String> runAPQLQuery(String queryPQL, List<String> IDs, String userID);

    List<Detail> getDetails();

    /**
     * Check whether a particular process is indexed for PQL querying.
     *
     * @param id  the external identifier of a process model
     * @return the PQL indexing status of the process model with the given external identifier
     * @throws SQLException if the status can't be read from the PQL index
     */
    IndexStatus getIndexStatus(ExternalId id) throws SQLException;

    /**
     * Thrown by {@link #query} if the PQL query doesn't return results.
     */
    public static class QueryException extends Exception {

        public QueryException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Thrown by {@link #query} if the PQL query wasn't well-formed.
     */
    public static class QueryParsingException extends QueryException {

        private List<String> parseErrorMessages;

        public QueryParsingException(List<String> parseErrorMessages) {
            super("Unable to parse PQL", null);
            this.parseErrorMessages = parseErrorMessages;
        }

        public List<String> getParseErrorMessages() {
            return parseErrorMessages;
        }
    }
}
