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

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apromore.dao.jpa.PpmExplainationToJsonConverter;
import org.apromore.dao.jpa.PpmSchemaToJsonConverter;
import org.apromore.dao.jpa.PpmValidationToJsonConverter;

@Entity
@Table(name = "predictor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictorDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "log_id")
    private int logId;
    @Column(name = "name")
    private String name;
    @Column(name = "prediction_type")
    @Enumerated(EnumType.STRING)
    private PredictionType predictionType;
    @Column(name = "target_attribute")
    private String targetAttribute;
    @Column(name = "feature_encoding_type")
    @Enumerated(EnumType.STRING)
    private FeatureEncodingType featureEncodingType;
    @Column(name = "ppm_status")
    @Enumerated(EnumType.STRING)
    private PpmStatus ppmStatus;
    @Column(name = "ppm_schema")
    @Convert(converter = PpmSchemaToJsonConverter.class)
    private PpmSchema ppmSchema;
    @Column(name = "validation")
    @Convert(converter = PpmValidationToJsonConverter.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Validation validation;
    @Column(name = "explanation")
    @Convert(converter = PpmExplainationToJsonConverter.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Explanation explanation;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "prediction_predictor",
        joinColumns = @JoinColumn(name = "predictor_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "prediction_id", referencedColumnName = "id"))
    private List<PredictionDao> predictions = new ArrayList<>();
}
