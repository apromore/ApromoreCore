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
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.data;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExpressionType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.VarMappingFactsType;

/**
 * Converts the Input Mapping of a YAWL Task to Object references.
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class InputVarMappingHandler extends BaseVarMappingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputVarMappingHandler.class);

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {
        final TaskType task = getConvertedParent();
        final NetType parentNet = getContext().getNetForTaskId(task.getId());
        final ExternalTaskFactsType originalTask = (ExternalTaskFactsType) getOriginalParent();

        final String xQuery = getObject().getExpression().getQuery();
        final String mapsTo = getObject().getMapsTo();

        final List<ObjectType> variables = calculateUsedNetVariables(xQuery, parentNet);

        if (variables.isEmpty()) {
            // Unable to calculate a single used variable from xQuery expression, either too complex or a constant
            /*
             * if (isParentCompositeTask()) { // Point to possibly not yet converted Object in sub net final NetFactsType subnet = (NetFactsType)
             * getContext().getDecompositionByID(originalTask.getDecomposesTo().getId()); // Search Input Parameter in original YAWL Net (MUST exists
             * according to schema) for (final InputParameterFactsType inputParam : subnet.getInputParam()) { if
             * (inputParam.getName().equals(getObject().getMapsTo())) { variables.add(createArtificialObject(parentNet, getObject(),
             * inputParam.getType())); } } if (variables.isEmpty()) { throw new CanoniserException("Could not find Input Parameter with name " +
             * mapsTo + " in sub net " + subnet.getName()); } } else { if (getContext().getObjectByName(mapsTo, task) != null) { // Task Object final
             * ObjectType targetTaskObj = getContext().getObjectByName(mapsTo, task); variables.add(createArtificialObject(parentNet, getObject(),
             * ((SoftType) targetTaskObj).getType())); } else { throw new CanoniserException("Could not find Object with name " + mapsTo +
             * " for input mapping of Task " + task.getName()); } }
             */

            //getConvertedParent().getInputExpr().add(mapsTo + "=" + convertXQuery(xQuery));

        }

        // Create references to all Objects this mapping is refering to
        for (final ObjectType var : variables) {
            final ObjectRefType objectRef = createObjectRef(var, InputOutputType.INPUT, false, false);
            if (isParentCompositeTask()) {
                // Point to possibly not yet converted Object in sub net
                // objectRef.setMapsToObjectId(generateUUID(DATA_ID_PREFIX, ConversionUtils.buildObjectId(getConvertedParent().getSubnetId(),
                // mapsTo)));
            } else {
                if (getContext().getObjectByName(mapsTo, task) != null) {
                    // Task-Scope Object
                    // objectRef.setMapsToObjectId(getContext().getObjectByName(mapsTo, task).getId());
                } else {
                    throw new CanoniserException("Could not find Object with name " + mapsTo + " for input mapping of Task " + task.getName());
                }
            }
            objectRef.getAttribute().add(createExpressionAttribute(getObject().getExpression()));
            LOGGER.debug("Adding Object Reference for YAWL Task {} (Type: {}, Source: {}, Target: {})", new String[] {
                    getConvertedParent().getName(), objectRef.getType().toString(), objectRef.getObjectId(), null });
            task.getObjectRef().add(objectRef);
        }

    }

    private String convertXQuery(final String xQuery) {
        return null;
    }

    private boolean isParentCompositeTask() {
        return getConvertedParent().getSubnetId() != null;
    }

    private SoftType createArtificialObject(final NetType parentNet, final VarMappingFactsType mapping, final String targetType)
            throws CanoniserException {
        final SoftType canonicalObject = getContext().getCanonicalOF().createSoftType();
        // New ID as this Object was not known in YAWL before
        canonicalObject.setId(generateUUID());
        canonicalObject.setName("constant_" + mapping.getMapsTo());
        canonicalObject.setType(targetType);
        // Remember the Complex or Constant value
        canonicalObject.getAttribute().add(createExpressionAttribute(mapping.getExpression()));
        parentNet.getObject().add(canonicalObject);
        LOGGER.debug("Adding artificial Object to Net {} (Name: {}, Type: {})", new String[] { parentNet.getId(), canonicalObject.getName(),
                canonicalObject.getType() });
        getContext().addObjectForNet(canonicalObject, parentNet);
        return canonicalObject;
    }

    private TypeAttribute createExpressionAttribute(final ExpressionType expressionType) throws CanoniserException {
        // TODO change
        final TypeAttribute expressionAttr = getContext().getCanonicalOF().createTypeAttribute();
        expressionAttr.setName(ConversionUtils.YAWL_EXPRESSION_EXTENSION);
        try {
            expressionAttr.setAny(ConversionUtils
                    .marshalYAWLFragment(ConversionUtils.YAWL_EXPRESSION_EXTENSION, expressionType, ExpressionType.class));
        } catch (JAXBException e) {
            throw new CanoniserException("Failed to add the expression extension element", e);
        }
        return expressionAttr;
    }

}
