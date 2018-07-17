package org.apromore.service.logvisualizer.impl;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Arc {
    private int source, target;
    private Integer hashCode = null;

    public Arc(int source, int target) {
        this.source = source;
        this.target = target;
    }

    public int getSource() {
        return source;
    }

    public int getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Arc) {
            Arc arc = (Arc) obj;
            return source == arc.source && target == arc.target;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
            hashCode = hashCodeBuilder.append(source).append(target).hashCode();
        }
        return hashCode;
    }
}
