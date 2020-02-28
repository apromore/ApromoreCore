/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.junit.Before;
import org.junit.Test;

public class DefaultAbstractCanoniserUnitTest {

    private DefaultAbstractCanoniser defaultAbstractCanoniser;

    @Before
    public void setUp() {
        defaultAbstractCanoniser = new DefaultAbstractCanoniser() {

            @Override
            public PluginResult canonise(final InputStream nativeInput, final List<AnnotationsType> annotationFormat, final List<CanonicalProcessType> canonicalFormat,
                    final PluginRequest request) throws CanoniserException {
                throw new CanoniserException("not implemented");
            }

            @Override
            public PluginResult deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final OutputStream nativeOutput,
                    final PluginRequest request) throws CanoniserException {
                throw new CanoniserException("not implemented");
            }

            @Override
            public PluginResult createInitialNativeFormat(final OutputStream nativeOutput, final String processName, final String processVersion, final String processAuthor,
                    final Date processCreated, final PluginRequest request) {
                return null;
            }

            @Override
            public CanoniserMetadataResult readMetaData(final InputStream nativeInput, final PluginRequest request) {
                return null;
            }
        };
    }

    @Test
    public void testGetNativeType() {
        assertNotNull(defaultAbstractCanoniser);
        assertNull(defaultAbstractCanoniser.getNativeType());
    }

}
