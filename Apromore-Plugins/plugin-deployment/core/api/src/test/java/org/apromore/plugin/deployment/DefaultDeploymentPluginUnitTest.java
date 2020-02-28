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

package org.apromore.plugin.deployment;

import static org.junit.Assert.assertNull;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.junit.Test;

public class DefaultDeploymentPluginUnitTest {

    @Test
    public void testGetNativeType() {
        assertNull(new DefaultDeploymentPlugin() {

            @Override
            public PluginResult deployProcess(final CanonicalProcessType canonicalProcess, final AnnotationsType annotation, final PluginRequest request)
                    throws DeploymentException, PluginPropertyNotFoundException {
                return null;
            }

            @Override
            public PluginResult deployProcess(final CanonicalProcessType canonicalProcess, final PluginRequest request) throws DeploymentException,
                    PluginPropertyNotFoundException {
                return null;
            }
        }.getNativeType());
    }

}
