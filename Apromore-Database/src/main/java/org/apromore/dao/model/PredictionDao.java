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
