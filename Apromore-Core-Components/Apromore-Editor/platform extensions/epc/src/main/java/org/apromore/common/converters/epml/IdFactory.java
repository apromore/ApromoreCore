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

// Java 2 Standard packages
import java.math.BigInteger;
import java.util.BitSet;

/**
 * Generator for EPML identifier strings.
 *
 * In EPML, identifiers are positive integers.  There is one namespace to identify the EPCs, and each EPC has
 * its own separate namespace to identify its elements (events, functions, etc)..
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class IdFactory {

   /**
    * Counter indicating the previous generated identifier.

    * Contains {@link BigInteger#ZERO} before any identifiers have been generated.
    */
   private BigInteger lastGeneratedId = BigInteger.ZERO;

    /**
     * Which integers occur in the namespace.
     */
    private final BitSet idSet = new BitSet();

    /**
     * Generate a new EPML identifier.
     *
     * If the JSON identifier can be parsed as an integer which is not already in use, this method
     * will try to match JSON identifiers to EPML ones.
     *
     * @param jsonId  the value of the corresponding JSON identifier, or <code>null</code>
     * @return a positive integer, never <code>null</code>
     */
    public BigInteger newId(final String jsonId) {

        BigInteger id = null;
        try {
            id = new BigInteger(jsonId);
        } catch (NumberFormatException e) {}
        
        // Because there's a practically infinite supply of positive integers, the following loop ought to terminate
        while (id == null || idSet.get(id.intValue()) || id.signum() != 1) {
            synchronized (lastGeneratedId) {
                lastGeneratedId = lastGeneratedId.add(BigInteger.ONE);
                id = lastGeneratedId;
            }
        }

        assert !idSet.get(id.intValue());
        assert id != null;
        assert id.signum() == 1;

        idSet.set(id.intValue());
        return id;
    }
}
