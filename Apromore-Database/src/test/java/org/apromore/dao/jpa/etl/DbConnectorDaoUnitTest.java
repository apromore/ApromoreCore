package org.apromore.dao.jpa.etl;

import org.apromore.config.BaseTestClass;
import org.apromore.dao.DbConnectorRepository;
import org.apromore.dao.model.DbConnectorDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class DbConnectorDaoUnitTest extends BaseTestClass {
    @Autowired
    DbConnectorRepository dbConnectorRepository;

    @Test
    public void test() {
        // Given
        DbConnectorDao dbConnectorDao = new DbConnectorDao(true, "connection_id", "username", "password", "url", "database_schema", "port");
        // When
        dbConnectorDao = dbConnectorRepository.saveAndFlush(dbConnectorDao);
        // Then
        assertThat(dbConnectorDao.getId()).isNotNull();

        // Find
        DbConnectorDao dbConnectorDaoExpected = dbConnectorRepository.findById(dbConnectorDao.getId()).get();
        assertThat(dbConnectorDaoExpected).usingRecursiveComparison().isEqualTo(dbConnectorDao);

        // Delete
        dbConnectorRepository.deleteById(dbConnectorDao.getId());
        assertThat(dbConnectorRepository.existsById(dbConnectorDao.getId())).isFalse();
    }
 }
