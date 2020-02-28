/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Annotations.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Annotation
 */
@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Integer> {

    /**
     * Find the annotation record by it's native uri id.
     * @param nativeUri the native uri
     * @return the annotation, a list of them for all the different canonicals.
     */
    @Query("SELECT a FROM Annotation a WHERE a.natve.id = ?1")
    List<Annotation> findByUri(final Integer nativeUri);

    /**
     * Get the annotations format. this is just a string but contains the xml annotations Format.
     * @param processId the processId of the annotations format.
     * @param branchName the branch name of the annotations
     * @param versionNumber the process model version number
     * @param annName the name of the annotation to get
     * @return the XML as a string
     */
    @Query("SELECT a FROM Annotation a JOIN a.processModelVersion pmv JOIN pmv.processBranch pb JOIN pb.process p " +
            "WHERE p.id = ?1 AND pb.branchName = ?2 AND pmv.versionNumber = ?3 AND a.name = ?4")
    Annotation getAnnotation(final Integer processId, final String branchName, final String versionNumber, final String annName);

    /**
     * Find the native record by the branch it is associated with.
     * @param processId   the processId
     * @param branchName the branch name of the annotations
     * @param versionNumber the processModelVersion version
     * @return the native, a list of them for all the different annotations versions.
     */
    @Query("SELECT a FROM Annotation a JOIN a.processModelVersion pmv JOIN pmv.processBranch pb JOIN pb.process p " +
            "WHERE p.id = ?1 AND pb.branchName = ?2 AND pmv.versionNumber = ?3")
    List<Annotation> findAnnotationByCanonical(final Integer processId, final String branchName, final String versionNumber);


}
