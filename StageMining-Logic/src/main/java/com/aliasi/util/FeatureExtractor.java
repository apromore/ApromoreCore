/*
 * LingPipe v. 4.1.0
 * Copyright (C) 2003-2011 Alias-i
 *
 * This program is licensed under the Alias-i Royalty Free License
 * Version 1 WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Alias-i
 * Royalty Free License Version 1 for more details.
 *
 * You should have received a copy of the Alias-i Royalty Free License
 * Version 1 along with this program; if not, visit
 * http://alias-i.com/lingpipe/licenses/lingpipe-license-1.txt or contact
 * Alias-i, Inc. at 181 North 11th Street, Suite 401, Brooklyn, NY 11211,
 * +1 (718) 290-9170.
 */

package com.aliasi.util;

import java.util.Map;

/**
 * A <code>FeatureExtractor</code> provides a method of converting
 * generic input objects into feature vectors.  Features themselves
 * are represented as strings.  Feature vectors are typically very
 * sparse, so they are represented as maps from objects to numerical
 * values.  Numerical values may be any numerical type, such as
 * {@link Double}, {@link Float}, or {@link Integer}.
 *
 * <p>For linear classifiers, it is often convenient to include a
 * distinguished feature with a value of 1.0 as part of every vector.
 * This will allow linear classifiers to learn offsets.
 *
 * <p>The class {@link ObjectToDoubleMap} is useful for incrementally
 * constructing feature maps.  
 *
 * @author Bob Carpenter
 * @version 3.8
 * @since   Lingpipe3.8
 * @param <E> the type of object whose features are extracted
 */
public interface FeatureExtractor<E> {

    /**
     * Return the feature vector for the specified input.
     *
     * @param in Input object.
     * @return The feature vector for the specified input.
     */
    public Map<String,? extends Number> features(E in);

}