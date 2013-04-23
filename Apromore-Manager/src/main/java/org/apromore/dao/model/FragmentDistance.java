/**
 *
 */
package org.apromore.dao.model;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * The Fragment Distance.
 *
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Entity
@Table(name = "fragment_distance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fragmentVersionId1", "fragmentVersionId2"}))
@Configurable("fragmentDistance")
@Cacheable(true)
@Cache(type = CacheType.SOFT_WEAK, isolation = CacheIsolationType.SHARED, expiry = 60000, size = 5000, alwaysRefresh = true, disableHits = true, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class FragmentDistance implements Serializable {

    private Integer id;
    private FragmentVersion fragmentVersionId1;
    private FragmentVersion fragmentVersionId2;
    private double distance;


    /**
     * Public Constructor.
     */
    public FragmentDistance() {
    }


    /**
     * returns the Id of this Object.
     *
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the Id of this Object
     *
     * @param id the new Id.
     */
    public void setId(final Integer id) {
        this.id = id;
    }


    @ManyToOne
    @JoinColumn(name = "fragmentVersionId2")
    public FragmentVersion getFragmentVersionId2() {
        return this.fragmentVersionId2;
    }

    public void setFragmentVersionId2(FragmentVersion newFragmentVersionId2) {
        this.fragmentVersionId2 = newFragmentVersionId2;
    }

    @ManyToOne
    @JoinColumn(name = "fragmentVersionId1")
    public FragmentVersion getFragmentVersionId1() {
        return this.fragmentVersionId1;
    }

    public void setFragmentVersionId1(final FragmentVersion newFragmentVersionId1) {
        this.fragmentVersionId1 = newFragmentVersionId1;
    }

    @Column(name = "ged")
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
