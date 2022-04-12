/**
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prediction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionDao {
    @Id
    private Integer id;
    @Column(name = "table_name")
    private String tableName;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PpmStatus status;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "prediction_predictor",
        joinColumns = @JoinColumn(name = "prediction_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "predictor_id", referencedColumnName = "id"))
    private List<PredictorDao> predictors = new ArrayList<>();
}
