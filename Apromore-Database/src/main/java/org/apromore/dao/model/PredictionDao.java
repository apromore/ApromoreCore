package org.apromore.dao.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prediction")
@Data
@NoArgsConstructor
public class PredictionDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "log_id")
    private int logId;
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

    public PredictionDao(int logId, String tableName, PpmStatus status) {
        this.logId = logId;
        this.tableName = tableName;
        this.status = status;
    }
}
