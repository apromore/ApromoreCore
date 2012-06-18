package org.apromore.mapper;

import org.apromore.dao.model.NativeType;
import org.apromore.model.FormatType;
import org.apromore.model.NativeTypesType;

import java.util.List;

/**
 * Mapper helper class to convert from the DAO Model to the Webservice Model.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class NativeTypeMapper {

    /**
     * Convert from the DB (NativeType) to the WS model (NativeTypesType).
     * @param natTypes the list of SearchHistoriesType from the WebService
     * @return the set of SearchHistory dao model populated.
     */
    public static NativeTypesType convertFromNativeType(List<NativeType> natTypes) {
        NativeTypesType types = new NativeTypesType();
        FormatType formatType;

        for (NativeType natType : natTypes) {
            formatType = new FormatType();
            formatType.setExtension(natType.getExtension());
            formatType.setFormat(natType.getNatType());
            types.getNativeType().add(formatType);
        }

        return types;
    }

}
