/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
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

package org.apromore.service.impl;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.util.ArrayList;
import java.util.List;

import org.apromore.dao.NativeRepository;
import org.apromore.dao.NativeTypeRepository;
import org.apromore.dao.model.NativeType;
import org.apromore.service.impl.FormatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
class FormatServiceImplUnitTest {

    private FormatServiceImpl formatServiceImpl;

    private NativeTypeRepository nativeTypeRepository;

    @BeforeEach
    void setUp() {
        NativeRepository nativeRepository = createMock(NativeRepository.class);
        nativeTypeRepository = createMock(NativeTypeRepository.class);

        formatServiceImpl = new FormatServiceImpl(nativeRepository, nativeTypeRepository);
    }


    @Test
    void getAllFormats() {
        List<NativeType> natTypes = new ArrayList<>();

        expect(nativeTypeRepository.findAll()).andReturn(natTypes);
        replay(nativeTypeRepository);

        List<NativeType> serviceNatTypes = formatServiceImpl.findAllFormats();
        verify(nativeTypeRepository);
        assertThat(serviceNatTypes, equalTo(natTypes));
    }

    @Test
    void getFormat() {
        String type = "bobType";
        NativeType natType = new NativeType();
        natType.setNatType(type);

        expect(nativeTypeRepository.findNativeType(type)).andReturn(natType);
        replay(nativeTypeRepository);

        NativeType serviceNatType = formatServiceImpl.findNativeType(type);
        verify(nativeTypeRepository);
        assertThat(serviceNatType, equalTo(natType));
    }
}
