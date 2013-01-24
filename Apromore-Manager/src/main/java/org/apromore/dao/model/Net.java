package org.apromore.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * The persistent class for the net database table.
 */
@Entity
@Table(name = "net")
public class Net implements Serializable {

    private Integer id;
    private String netUri;

    private Process process;
    private ProcessModelVersion processModelVersion;


    /**
     * Default Constructor.
     */
    public Net() { }


    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer newId) {
        this.id = newId;
    }


    @Column(name = "netUri", unique = true, nullable = false, length = 200)
    public String getNetUri() {
        return this.netUri;
    }

    public void setNetUri(String newNetUri) {
        this.netUri = newNetUri;
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