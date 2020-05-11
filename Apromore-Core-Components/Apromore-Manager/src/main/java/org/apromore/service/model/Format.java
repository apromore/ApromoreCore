/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.service.model;

import javax.activation.DataSource;

/**
 * Stores the Canonical Format and the Annotation.
 * NOTE: This isn't persisted to the DB, it is used as a value object to pass objects around.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class Format {

    private DataSource cpf;
    private DataSource anf;

    /**
     * Public Constructor.
     */
    public Format() {
    }


    /**
     * Returns the Canonical process model.
     *
     * @return the model in canonical format
     */
    public DataSource getCpf() {
        return cpf;
    }

    /**
     * Sets the Canonical format.
     *
     * @param cpf the canonical format
     */
    public void setCpf(DataSource cpf) {
        this.cpf = cpf;
    }

    /**
     * Returns the Annotation.
     *
     * @return the annotation
     */
    public DataSource getAnf() {
        return anf;
    }

    /**
     * Sets the Annotation.
     *
     * @param anf the annotation
     */
    public void setAnf(DataSource anf) {
        this.anf = anf;
    }
}
