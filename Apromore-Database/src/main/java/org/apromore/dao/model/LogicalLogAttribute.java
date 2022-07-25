/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

package org.apromore.dao.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Configurable;



@Entity
@Table(name = "logical_log_attribute",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
    }
)
@Configurable("logical_log_attribute")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogicalLogAttribute extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "event_log_id", referencedColumnName = "id", nullable = false)
    Log log;

    @Column(name = "logical_name")
    private String logicalName;

    @Setter
    @OneToOne(mappedBy = "logicalLogAttribute", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    PhysicalLogAttribute physicalLogAttribute;

    @ManyToOne
    @JoinColumn(name = "attribute_type_id", nullable = false)
    private AttributeTypeDao attributeTypeDao;

    @ManyToOne
    @JoinColumn(name = "data_type_id", nullable = false)
    private DataTypeDao dataTypeDao;

    @Column(name = "date_format")
    private String dateFormat;

    @Column(name = "is_masked")
    private boolean isMasked;

    @Column(name = "attribute_index", nullable = false)
    private int index;

    @Column(name = "is_perspective", nullable = false)
    private boolean isPerspective;
}
