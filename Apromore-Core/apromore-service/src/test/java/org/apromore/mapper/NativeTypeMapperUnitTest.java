package org.apromore.mapper;

import org.apromore.dao.model.NativeType;
import org.apromore.model.NativeTypesType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * SearchHistory Mapper Unit test.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class NativeTypeMapperUnitTest {

    NativeTypeMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new NativeTypeMapper();
    }

    @Test
    public void testConvertFromNativeType() throws Exception {
        List<NativeType> natTypes = new ArrayList<NativeType>();
        NativeType typ1 = new NativeType();
        typ1.setExtension("ext");
        typ1.setNatType("bobs");
        natTypes.add(typ1);

        NativeType typ2 = new NativeType();
        typ2.setExtension("cat");
        typ2.setNatType("xpdl");
        natTypes.add(typ2);

        NativeTypesType type = mapper.convertFromNativeType(natTypes);
        assertThat(type.getNativeType().size(), equalTo(natTypes.size()));
        assertThat(type.getNativeType().get(0).getFormat(), equalTo(natTypes.get(0).getNatType()));
        assertThat(type.getNativeType().get(0).getExtension(), equalTo(natTypes.get(0).getExtension()));
    }

}
