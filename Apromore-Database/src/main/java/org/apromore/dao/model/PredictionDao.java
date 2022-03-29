package org.apromore.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

    @Column(name = "save_location")
    private String saveLocation;

    @Column(name = "table_name")
    private String tableName;



    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PpmStatus ppmStatus;
}
