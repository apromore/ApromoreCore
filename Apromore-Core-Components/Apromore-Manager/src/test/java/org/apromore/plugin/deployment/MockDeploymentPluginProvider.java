/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013, 2015 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.deployment;

import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.ParameterType;

/**
 * Mock used to trick the tests that this class from the osgi context actually does exist.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class MockDeploymentPluginProvider implements DeploymentPlugin {

    @Override
    public String getNativeType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PluginResult deployProcess(CanonicalProcessType canonicalProcess, PluginRequest request) throws DeploymentException, PluginPropertyNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PluginResult deployProcess(CanonicalProcessType canonicalProcess, AnnotationsType annotation, PluginRequest request) throws DeploymentException, PluginPropertyNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<ParameterType<?>> getAvailableParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<ParameterType<?>> getMandatoryParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<ParameterType<?>> getOptionalParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getVersion() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getAuthor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getEMail() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
