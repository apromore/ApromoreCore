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
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.resource;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.ResourceTypeType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;

public class AutomatedTaskResourceingHandler extends ResourceingHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.yawl.resource.ResourceingHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        String automaticName = "Unkown Service or Codelet";

        final ExternalTaskFactsType parentTask = (ExternalTaskFactsType) getOriginalParent();

        if (parentTask.getDecomposesTo() != null) {
            final WebServiceGatewayFactsType decomposition = (WebServiceGatewayFactsType) getContext().getDecompositionByID(
                    parentTask.getDecomposesTo().getId());
            if (decomposition.getCodelet() != null) {
                automaticName = decomposition.getCodelet();
            } else if (decomposition.getYawlService() != null) {
                automaticName = decomposition.getYawlService().getOperationName() + " using WSDL " + decomposition.getYawlService().getWsdlLocation();
            }
        }

        final ResourceTypeType resourceType = CPF_FACTORY.createSoftwareSystemType();
        resourceType.setId(generateUUID());
        resourceType.setOriginalID(null);
        resourceType.setName(automaticName);
        getContext().getCanonicalResult().getResourceType().add(resourceType);
        createResourceReference(resourceType, null);
    }

}
