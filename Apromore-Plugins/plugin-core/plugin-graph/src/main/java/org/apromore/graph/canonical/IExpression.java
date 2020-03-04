/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2013 Felix Mannhardt.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.graph.canonical;

/**
 * Interface to a flow relation of the Canonical format.
 * 
 * @author Cameron James
 */
public interface IExpression {

    /**
     * Get the Description.
     * @return the description
     */
    String getDescription();

    /**
     * Set the Description.
     * @param newDescription the new Description
     */
    void setDescription(String newDescription);

    /**
     * Get the Language.
     * @return the Language
     */
    String getLanguage();

    /**
     * Set the Language.
     * @param newLanguage the new Language
     */
    void setLanguage(String newLanguage);

    /**
     * Get the Expression.
     * @return the Expression
     */
    String getExpression();

    /**
     * Set the Expression.
     * @param newExpression the new Expression
     */
    void setExpression(String newExpression);

    /**
     * Get the ReturnType.
     * @return the ReturnType
     */
    String getReturnType();

    /**
     * Set the ReturnType.
     * @param newReturnType the new ReturnType
     */
    void setReturnType(String newReturnType);
}