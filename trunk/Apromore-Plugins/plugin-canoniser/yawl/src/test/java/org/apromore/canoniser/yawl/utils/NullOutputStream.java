package org.apromore.canoniser.yawl.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Does nothing
 */
public class NullOutputStream extends OutputStream {

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException {
    }

}
