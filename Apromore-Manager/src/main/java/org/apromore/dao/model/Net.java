package org.apromore.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * The persistent class for the net database table.
 */
@Entity
@Table(name = "net")
public class Net implements Serializable {

    private String id;
    private Process process;
    private ProcessModelVersion processModelVersion;


    /**
     * Default Constructor.
     */
    public Net() { }


    @Id
    @Column(name = "id", unique=true, nullable=false, length = 200)
    public String getId() {
        return this.id;
    }

    public void setId(String newId) {
        this.id = newId;
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
    @JoinColumn(name = "processModelVersionId", nullable = false)
    public ProcessModelVersion getProcessModelVersion() {
        return this.processModelVersion;
    }

    public void setProcessModelVersion(ProcessModelVersion processModelVersion) {
        this.processModelVersion = processModelVersion;
    }

}