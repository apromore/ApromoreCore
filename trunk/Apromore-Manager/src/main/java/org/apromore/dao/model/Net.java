package org.apromore.dao.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the net database table.
 */
@Entity
@Table(name = "net")
public class Net implements Serializable {

    private Integer processModelVersionId;
    private String netId;

    private Process process;
    private ProcessModelVersion processModelVersion;


    /**
     * Default Constructor.
     */
    public Net() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "processModelVersionId", unique = true, nullable = false)
    public Integer getProcessModelVersionId() {
        return processModelVersionId;
    }

    /**
     * Set the id for the Object.
     * @param newId The role name to set.
     */
    public void setProcessModelVersionId(final Integer newId) {
        this.processModelVersionId = newId;
    }


    @Column(length = 200)
    public String getNetId() {
        return this.netId;
    }

    public void setNetId(String netId) {
        this.netId = netId;
    }


    @ManyToOne
    @JoinColumn(name = "processId")
    public Process getProcess() {
        return this.process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }


    @OneToOne
    @PrimaryKeyJoinColumn(name = "processModelVersionId", referencedColumnName = "id")
    public ProcessModelVersion getProcessModelVersion() {
        return this.processModelVersion;
    }

    public void setProcessModelVersion(ProcessModelVersion processModelVersion) {
        this.processModelVersion = processModelVersion;
    }

}