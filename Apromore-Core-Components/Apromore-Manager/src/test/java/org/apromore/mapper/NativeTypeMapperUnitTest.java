/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011, 2012, 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
