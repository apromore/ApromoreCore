package org.apromore.dao.model;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "statistic",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"}),
        }
)
@Configurable("statistic")
@Cache(expiry = 180000, size = 5000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Statistic implements Serializable {

    private byte[] id;
    private byte[] pid;
    private Integer logid;
    private String stat_key;
    private String stat_value;

    /**
     * Default constructor.
     */
    public Statistic() {
        super();
    }

    public Statistic(Integer id) {
        id = id;
    }

    @Id
    @Column(name = "id", unique = true, nullable = false, length = 16)
    public byte[] getId() {
        return this.id;
    }
    public void setId(final byte[] id) {
        this.id = id;
    }

    @Column(name = "pid", length = 16)
    public byte[] getPid() {
        return pid;
    }
    public void setPid(final byte[] pid) {
        this.pid = pid;
    }

    @Column(name = "logid")
    public Integer getLogid() {
        return logid;
    }
    public void setLogid(final Integer logid) {
        this.logid = logid;
    }

    @Column(name = "stat_key", length = 1023)
    public String getStat_key() {
        return stat_key;
    }
    public void setStat_key(final String stat_key) {
        this.stat_key = stat_key;
    }

    @Column(name = "stat_value", length = 1023)
    public String getStat_value() {
        return stat_value;
    }
    public void setStat_value(final String stat_value) {
        this.stat_value = stat_value;
    }

}
