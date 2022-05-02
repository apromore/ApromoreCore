package org.apromore.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Table(name = "subprocess_process",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
    })
@Configurable("subprocess_process")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubprocessProcess {
    @Id
    private Integer id;
    @Column(name = "subprocess_id", nullable = false)
    private String subprocessId;
    @OneToOne
    @JoinColumn(name = "subprocess_parent_id", nullable = false)
    private Process subprocessParent;
    @OneToOne
    @JoinColumn(name = "linked_process_id", nullable = false)
    private Process linkedProcess;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
