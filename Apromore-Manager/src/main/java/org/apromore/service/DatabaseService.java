package org.apromore.service;

import java.util.List;

/**
 * Created by corno on 28/08/2014.
 */
public interface DatabaseService {

    List<String> getLabels(String table, String columnName);
}
