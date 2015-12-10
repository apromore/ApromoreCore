/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.common.converters.epml.handler.epml.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apromore.common.converters.epml.context.EPMLConversionContext;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;

import de.epml.TypeAttribute;
import de.epml.TypePosition;
import de.epml.TypeRole;

public class TypeRoleHandler extends NodeHandler {

    private final TypeRole role;

    public TypeRoleHandler(final EPMLConversionContext context, final TypeRole role) {
        super(context);
        this.role = role;
    }

    @Override
    protected Map<String, String> convertProperties() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("title", role.getName());
        hashMap.put("description", role.getDescription());
        return hashMap;
    }

    @Override
    protected TypePosition getPosition() {
        if (role.getGraphics() != null) {
            return role.getGraphics().getPosition();
        } else {
            TypePosition position = new de.epml.ObjectFactory().createTypePosition();
            position.setX(BigDecimal.valueOf(0));
            position.setY(BigDecimal.valueOf(30));
            position.setWidth(BigDecimal.valueOf(100));
            position.setHeight(BigDecimal.valueOf(60));
            return position;
        }
    }

    @Override
    protected BasicShape createShape() {
        for (TypeAttribute attribute : role.getAttribute()) {
            if ("roletype".equals(attribute.getTypeRef())) {
                switch (attribute.getValue()) {
                case "IT system":
                    return new BasicNode(getShapeId().toString(), "System");
                case "Organizational Unit":
                    return new BasicNode(getShapeId().toString(), "Organization");
                }
            }
        }

        return new BasicNode(getShapeId().toString(), "Position");
    }

    @Override
    protected BigInteger getShapeId() {
        return role.getId();
    }

}
