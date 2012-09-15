/**
 *
 */
package org.apromore.dao.model;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * The Fragment Distance.
 *
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Entity
@Table(name = "fragment_distance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fragmentVersionId1", "fragmentVersionId2"}))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("fragmentDistance")
public class FragmentDistance implements Serializable {

    private Integer id;
    private FragmentVersion fragmentVersionId1;
    private FragmentVersion fragmentVersionId2;
    private double distance;


    /**
     * Public Constructor.
     */
    public FragmentDistance() { }


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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fragmentVersionId2", nullable = false)
    public FragmentVersion getFragmentVersionId2() {
        return this.fragmentVersionId2;
    }

    public void setFragmentVersionId2(FragmentVersion newFragmentVersionId2) {
        this.fragmentVersionId2 = newFragmentVersionId2;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fragmentVersionId1", nullable = false)
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
