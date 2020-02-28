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
package org.apromore.canoniser.provider;

import java.util.Set;

import org.apromore.canoniser.Canoniser;
import org.apromore.plugin.exception.PluginNotFoundException;

/**
 * Canoniser API used by Apromore, to access the Canoniser Plugins
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface CanoniserProvider {

    /**
     * List all available Canoniser
     *
     * @return Set of Canoniser
     */
    Set<Canoniser> listAll();

    /**
     * List all available Canoniser converting the specified native type.
     *
     * @param nativeType
     *            for example "EPML 2.0" or "YAWL 2.2"
     * @return Set of Canoniser
     */
    Set<Canoniser> listByNativeType(String nativeType);

    /**
     * List all available Canoniser converting the specified native type with the exact name. Please note there could be multiple versions installed,
     * so a Collection will be returned.
     *
     * @param nativeType
     *            for example "EPML 2.0" or "YAWL 2.2"
     * @param name
     *            usually the full class name of the Canoniser
     * @return Set of Canoniser
     */
    Set<Canoniser> listByNativeTypeAndName(String nativeType, String name);

    /**
     * Return the first Canoniser that is found with the given parameters.
     *
     * @param nativeType
     *            for example "EPML 2.0" or "YAWL 2.2"
     * @return Canoniser for given native type
     * @throws PluginNotFoundException
     *             in case there is no Canoniser found
     */
    Canoniser findByNativeType(String nativeType) throws PluginNotFoundException;

    /**
     * Return the first Canoniser that is found with the given parameters.
     *
     * @param nativeType
     *            for example "EPML 2.0" or "YAWL 2.2"
     * @param name
     *            usually the full class name of the Canoniser
     * @return Canoniser for given native type and name
     * @throws PluginNotFoundException
     *             in case there is no Canoniser found
     */
    Canoniser findByNativeTypeAndName(String nativeType, String name) throws PluginNotFoundException;

    /**
     * Return the first Canoniser that is found with the given parameters.
     *
     * @param nativeType
     *            for example "EPML 2.0" or "YAWL 2.2"
     * @param name
     *            usually the full class name of the Canoniser
     * @param version
     *            usually the bundle version (X.Y.Z-CLASSIFIER)
     * @return Canoniser for given native type and name and version
     * @throws PluginNotFoundException
     *             in case there is no Canoniser found
     */
    Canoniser findByNativeTypeAndNameAndVersion(String nativeType, String name, String version) throws PluginNotFoundException;

}
