package org.apromore.property;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: cameron
 * Date: 21/08/13
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {

    public boolean fileNotExists(String filename) {
        return isBlank(filename) || !new File(filename).exists();
    }

    public boolean isAbsolutePath(String file) {
        return new File(file).isAbsolute();
    }

}
