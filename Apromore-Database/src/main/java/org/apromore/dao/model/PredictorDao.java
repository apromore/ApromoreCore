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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "predictor")
@Data
@NoArgsConstructor
public class PredictorDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "log_id")
    private int logId;
    @Column(name = "name")
    private String name;
    @Column(name = "prediction_type")
    @Enumerated(EnumType.STRING)
    private PredictionType predictionType;
    @Column(name = "target_attribute")
    private String targetAttribute;
    @Column(name = "ppm_status")
    @Enumerated(EnumType.STRING)
    private PpmStatus ppmStatus;

    public PredictorDao(int logId, String name, PredictionType predictionType, String targetAttribute,
                        PpmStatus ppmStatus) {
        this.logId = logId;
        this.name = name;
        this.predictionType = predictionType;
        this.targetAttribute = targetAttribute;
        this.ppmStatus = ppmStatus;
    }
}
