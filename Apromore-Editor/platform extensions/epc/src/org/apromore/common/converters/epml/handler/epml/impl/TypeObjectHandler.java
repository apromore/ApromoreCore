package org.apromore.common.converters.epml.handler.epml.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apromore.common.converters.epml.context.EPMLConversionContext;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;

import de.epml.TypePosition;

public class TypeObjectHandler extends NodeHandler {

    private final de.epml.TypeObject object;

    public TypeObjectHandler(final EPMLConversionContext context, final de.epml.TypeObject object) {
        super(context);
        this.object = object;
    }

    @Override
    protected Map<String, String> convertProperties() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("title", object.getName());
        hashMap.put("description", object.getDescription());
        hashMap.put("type", object.getType());
        return hashMap;
    }

    @Override
    protected TypePosition getPosition() {
        if (object.getGraphics() != null) {
            return object.getGraphics().getPosition();
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
        return new BasicNode(getShapeId().toString(), "Data");
    }

    @Override
    protected BigInteger getShapeId() {
        return object.getId();
    }

}
