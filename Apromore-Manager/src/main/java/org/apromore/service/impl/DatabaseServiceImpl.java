package org.apromore.service.impl;

import org.apromore.service.DatabaseService;
import org.apromore.service.MySqlBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by corno on 28/08/2014.
 */
@Service
public class DatabaseServiceImpl implements DatabaseService {
    private MySqlBean mySqlBean;
    private static final String tableLabels = "pql.jbpt_labels";
    private static final String tableNets = "pql.jbpt_petri_nets";
    private static final String columnLabel = "label";
    private static final String columIdentifier = "identifier";
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    public DatabaseServiceImpl(){

    }

    @Inject
    public DatabaseServiceImpl(MySqlBean mySqlBean){
        this.mySqlBean=mySqlBean;
    }

    @Override
    public List<String> getLabels(String table, String columnName) {
        HashSet<String> labels = new HashSet<String>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(mySqlBean.getURL(), mySqlBean.getUser(), mySqlBean.getPassword());
            Statement stmt = conn.createStatement();
            String query = "SELECT "+columnName+" FROM "+table+";";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
                labels.add(rs.getString(columnName));
        }catch(Exception ex){
            LOGGER.error("-----------ERRORRE: " + ex.toString());
            for (StackTraceElement ste : ex.getStackTrace())
                LOGGER.info("ERRORE: " + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + " " + ste.getFileName());
        }
        return new LinkedList<String>(labels);
    }
}
