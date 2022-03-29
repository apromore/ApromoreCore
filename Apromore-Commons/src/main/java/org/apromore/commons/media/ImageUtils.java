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

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Various utility for image
 */
public final class ImageUtils {

    private ImageUtils() {
        throw new IllegalStateException("Image utility class");
    }

    public static BufferedImage getImage(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }

    public static Dimension getImageDimension(InputStream inputStream) throws IOException {
        BufferedImage img = ImageUtils.getImage(inputStream);
        return new Dimension(img.getWidth(), img.getHeight());
    }

    public static double getImageRatio(InputStream inputStream) throws IOException {
        Dimension dim = ImageUtils.getImageDimension(inputStream);
        return dim.getWidth()/dim.getHeight();
    }
}
