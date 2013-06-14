package org.apromore.toolbox.clustering.dissimilarity.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apromore.graph.canonical.CPFNode;

/**
 * Simple Edge representation used in the GED Calc.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class GEDEdge implements Comparable<GEDEdge> {

    private CPFNode source;
    private CPFNode target;

    public GEDEdge(final CPFNode n1, final CPFNode n2) {
        source = n1;
        target = n2;
    }

    public CPFNode getSource() {
        return source;
    }

    public CPFNode getTarget() {
        return target;
    }


    @Override
    public String toString() {
        return "(" + getSource().getId() + "," + getTarget().getId() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GEDEdge)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final GEDEdge otherObject = (GEDEdge) obj;

        return new EqualsBuilder()
                .append(this.source.getId(), otherObject.source.getId())
                .append(this.target.getId(), otherObject.target.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.source).append(this.target).toHashCode();
    }

    @Override
    public int compareTo(GEDEdge o) {
        return equals(o) ? 0 : 1;
    }
}
