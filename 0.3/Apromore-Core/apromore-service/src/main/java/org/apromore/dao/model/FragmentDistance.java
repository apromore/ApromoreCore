/**
 *
 */
package org.apromore.dao.model;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author Chathura C. Ekanayake
 */
@Entity
@Table(name = "fragment_distance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("fragmentDistance")
public class FragmentDistance implements Serializable {

    private FragmentDistanceId id;
    private double distance;

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "fragmentId1", column = @Column(name = "fid1", nullable = false, length = 40)),
            @AttributeOverride(name = "fragmentId2", column = @Column(name = "fid2", nullable = false, length = 40))})
    public FragmentDistanceId getId() {
        return this.id;
    }

    public void setId(FragmentDistanceId id) {
        this.id = id;
    }

    @Column(name = "ged")
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
