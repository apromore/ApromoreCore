package org.apromore.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "db_connector")
@Data
@NoArgsConstructor
public class DbConnectorDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "created")
    private boolean created;
    @Column(name = "connection_key")
    private String connectionKey;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "url")
    private String url;
    @Column(name = "database_schema")
    private String databaseSchema;
    @Column(name = "port")
    private String port;

    public DbConnectorDao(boolean created, String connectionKey, String username, String password, String url, String databaseSchema, String port) {
        this.created = created;
        this.connectionKey = connectionKey;
        this.username = username;
        this.password = password;
        this.url = url;
        this.databaseSchema = databaseSchema;
        this.port = port;
    }
}
