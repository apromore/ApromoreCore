package org.apromore.portal.client;

import org.apromore.model.Detail;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ResultPQL;

import java.util.List;

/**
 * Created by corno on 9/07/2014.
 */
public interface PortalService {
    public void addNewTab(List<ResultPQL> results, String userID, List<Detail> details, String query, String nameQuery);
}
