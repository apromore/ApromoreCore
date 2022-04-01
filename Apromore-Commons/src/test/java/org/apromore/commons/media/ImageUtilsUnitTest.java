/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.commons.media;

import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Dimension;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

/**
 * Various test utility for image
 */
final class ImageUtilsUnitTest {
    private static String TEST_IMAGE_1 = "/image-10x10.png";
    private static String TEST_IMAGE_2 = "/image-20x10.jpg";

    @Test
    void getImageDimension_ShouldReturnCorrectDimension() throws Exception {
        InputStream is = this.getClass().getResourceAsStream(TEST_IMAGE_1);
        Dimension dim = ImageUtils.getImageDimension(is);
        if (dim.getWidth() != 10 || dim.getHeight() != 10) {
            fail("Invalid dimension " + TEST_IMAGE_1);
        }
    }

    @Test
    void getImageRatio_ShouldReturnCorrectRatio() throws Exception {
        InputStream is = this.getClass().getResourceAsStream(TEST_IMAGE_2);
        if (ImageUtils.getImageRatio(is) != 2.0) {
            fail("Invalid ratio for " + TEST_IMAGE_2);
        }
    }
}
