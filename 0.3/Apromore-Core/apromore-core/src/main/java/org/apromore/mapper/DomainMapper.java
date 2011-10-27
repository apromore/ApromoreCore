package org.apromore.mapper;

import org.apromore.dao.model.NativeType;
import org.apromore.model.DomainsType;
import org.apromore.model.FormatType;
import org.apromore.model.NativeTypesType;

import java.util.List;

/**
 * Mapper helper class to convert from the DAO Model to the Webservice Model.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class DomainMapper {

    /**
     * Convert from the a list (String) to the WS model (DomainsType).
     * @param domains the list of SearchHistoriesType from the WebService
     * @return the DomainsType ready for transport to the calling system.
     */
    public static DomainsType convertFromDomains(List<String> domains) {
        DomainsType types = new DomainsType();
        types.getDomain().addAll(domains);
        return types;
    }

}
