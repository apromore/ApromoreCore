package org.apromore.dao.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Table(name = "edge_mapping",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fragmentVersionId", "edgeId"})
        }
)
@Configurable("edgeMapping")
@Cache(expiry = 180000, size = 10000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class EdgeMapping {

    private Integer id;

    private FragmentVersion fragmentVersion;
    private Edge edge;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "fragmentVersionId")
    public FragmentVersion getFragmentVersion() {
        return fragmentVersion;
    }

    public void setFragmentVersion(final FragmentVersion fragmentVersion) {
        this.fragmentVersion = fragmentVersion;
    }

    @ManyToOne
    @JoinColumn(name = "edgeId")
    public Edge getEdge() {
        return edge;
    }

    public void setEdge(final Edge edge) {
        this.edge = edge;
    }
}
