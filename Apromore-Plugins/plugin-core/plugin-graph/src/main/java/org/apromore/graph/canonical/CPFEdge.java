/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2013 Felix Mannhardt.
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

package org.apromore.graph.canonical;

import org.jbpt.graph.abs.AbstractMultiDirectedGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface to a Canonical Edge.
 *
 * @author Cameron James
 */
public class CPFEdge extends AbstractEdge<CPFNode> implements IEdge<CPFNode> {

    private String originalId;
    private boolean isDefault = false;
    private CPFExpression conditionExpr;
    private Map<String, IAttribute> attributes = new HashMap<String, IAttribute>(0);


    /* Standard constructor based on jBPT implementation. */
    public CPFEdge(AbstractMultiDirectedGraph<?, CPFNode> g, CPFNode source, CPFNode target) {
        super(g, source, target);
    }

    /* We Need to set the id sometimes. */
    public CPFEdge(AbstractMultiDirectedGraph<?, CPFNode> g, String id, CPFNode source, CPFNode target) {
        super(g, source, target);

        originalId = id;
    }

    /* We need to set the whole object data sometimes... */
    public CPFEdge(AbstractMultiDirectedGraph<?, CPFNode> g, CPFEdge edge) {
        super(g, edge.getSource(), edge.getTarget());

        originalId = edge.getOriginalId();
        isDefault = edge.isDefault();
        conditionExpr = edge.getConditionExpr();
        attributes = edge.getAttributes();
    }


    @Override
    public void setOriginalId(final String newOriginalId) {
        originalId = newOriginalId;
    }

    @Override
    public String getOriginalId() {
        return originalId;
    }

    @Override
    public void setDefault(final boolean newDefault) {
        isDefault = newDefault;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public void setConditionExpr(final CPFExpression newConditionExpr) {
        conditionExpr = newConditionExpr;
    }

    @Override
    public CPFExpression getConditionExpr() {
        return conditionExpr;
    }

    @Override
    public void addAttribute(final String name, final String value, final Object any) {
        attributes.put(name, new CPFAttribute(value, any));
    }

    @Override
    public void addAttribute(final String name, final String value) {
        addAttribute(name, value, null);
    }

    @Override
    public void setAttributes(final Map<String, IAttribute> newAttributes) {
        attributes = newAttributes;
    }

    @Override
    public Map<String, IAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public IAttribute getAttribute(final String name) {
        return attributes.get(name);
    }
}
