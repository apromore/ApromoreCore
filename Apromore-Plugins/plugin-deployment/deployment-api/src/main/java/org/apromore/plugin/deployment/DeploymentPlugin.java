/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.plugin.deployment;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.MessageAwarePlugin;
import org.apromore.plugin.PropertyAwarePlugin;
import org.apromore.plugin.deployment.exception.DeploymentException;

/**
 * Interface for Deployment Plugins, that support deploying processes to a process/work-flow engine.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public interface DeploymentPlugin extends MessageAwarePlugin, PropertyAwarePlugin {

    /**
     * Native type the Deployment Plugin uses
     *
     * @return native type the process will be deployed to
     */
    String getNativeType();

	/**
	 * Deploys the process in canonical process format to a process/work-flow engine.
	 *
	 * @param canonicalProcess the process to be deployed
     * @throws DeploymentException in case of an error during deployment
	 */
	void deployProcess(CanonicalProcessType canonicalProcess) throws DeploymentException;

	/**
     * Deploys the process in canonical process format to a process/work-flow engine.
     *
     * @param canonicalProcess the process to be deployed
     * @param annotation to be used during deployment
     * @throws DeploymentException in case of an error during deployment
     */
    void deployProcess(CanonicalProcessType canonicalProcess, AnnotationsType annotation) throws DeploymentException;

}