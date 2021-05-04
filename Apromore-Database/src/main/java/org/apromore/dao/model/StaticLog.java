package org.apromore.dao.model;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "static_log")
@Configurable("static_log")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
@Setter
public class StaticLog {
    private Long id;
    private Boolean isParquetOrigin;
    private String tableName;
    private String s3Location;
    private String schemaInfo;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    @Column(name = "parquet_origin")
    public Boolean getParquetOrigin() {
        return isParquetOrigin;
    }

    @Column(name = "table_name")
    public String getTableName() {
        return tableName;
    }

    @Column(name = "s3_location")
    public String getS3Location() {
        return s3Location;
    }

    @Column(name = "schema_info")
    public String getSchemaInfo() {
        return schemaInfo;
    }
}
