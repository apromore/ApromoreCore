package org.apromore.service;

import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.helper.Version;
import org.apromore.model.ProcessSummariesType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.model.Detail;

import java.util.List;
import java.util.Set;

/**
 * Created by corno on 2/07/2014.
 */
public interface PQLService extends Observer{

    void indexAllModels();
    void indexOneModel(ProcessModelVersion pmv);

    void deleteModel(ProcessModelVersion pmv);
    List<String> runAPQLQuery(String queryPQL,List<String> IDs,String userID);
    List<Detail> getDetails();

}
