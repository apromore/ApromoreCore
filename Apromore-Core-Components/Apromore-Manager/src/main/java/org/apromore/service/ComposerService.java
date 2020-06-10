/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

/**
 *
 */
package org.apromore.service;

import org.apromore.dao.model.FragmentVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.graph.canonical.Canonical;

/**
 * Composes from the DB representation into a RPST Directed Graph.
 * @author Chathura Ekanayake
 */
public interface ComposerService {

    /**
     * Compose from the Apromore DB version to CPF RPST Directed Graph.
     * @param rootFragment the fragment version root object
     * @return the Directed Graph
     * @throws ExceptionDao if there is a DB Exception
     */
    public Canonical compose(FragmentVersion rootFragment) throws ExceptionDao;

    /**
     * Compose from the Apromore DB version to CPF RPST Directed Graph.
     * @param rootFragmentId the fragment version root id
     * @return the Directed Graph
     * @throws ExceptionDao if there is a DB Exception
     */
    public Canonical compose(Integer rootFragmentId) throws ExceptionDao;

}
