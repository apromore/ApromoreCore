/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.dao.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @Column(name = "subprocess_id", nullable = false)
    private String subprocessId;
    @OneToOne
    @JoinColumn(name = "subprocess_parent_id", nullable = false)
    private Process subprocessParent;
    @OneToOne
    @JoinColumn(name = "linked_process_id", nullable = false)
    private Process linkedProcess;
    @OneToOne
    @JoinColumn(name = "linked_process_model_version_id")
    private ProcessModelVersion linkedProcessModelVersion;

}
