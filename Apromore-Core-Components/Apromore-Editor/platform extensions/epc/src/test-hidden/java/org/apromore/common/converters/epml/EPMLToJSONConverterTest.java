/**
 * Copyright (c) 2013 Simon Raboczi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * See: http://www.opensource.org/licenses/mit-license.php
 *
 */
package org.apromore.common.converters.epml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

/**
 * Test suite for {@link EPMLToJSONConverter}.
 *
 * @author Simon Raboczi
 */
public class EPMLToJSONConverterTest {

    /**
     * Test the {@link EPMLToJSONConverter#convert} method.
     */
    @Test
    public void testConvert() throws Exception {
        EPMLToJSONConverter   converter = new EPMLToJSONConverter();
        InputStream           in        = new FileInputStream("Apromore-Editor/tests/data/object.epml");
        ByteArrayOutputStream out       = new ByteArrayOutputStream();
        converter.convert(in, out);

        JSONToEPMLConverter   converter2 = new JSONToEPMLConverter();
        InputStream           in2        = new ByteArrayInputStream(out.toByteArray());
        ByteArrayOutputStream out2       = new ByteArrayOutputStream();

        //converter2.convert(in2, out2);
    }
}
