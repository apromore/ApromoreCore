package org.apromore.filestore.client;

// Java 2 Standard Edition classes
import java.io.File;
import java.util.Date;

// Third party classes
import com.github.sardine.DavResource;

/**
 * A file on the WebDAV repository, managed by a {@link DavFileSystemView}.
 *
 * This class must have <em>every</em> method of the {@link File} superclass overridden to eliminate platform-specific filesystem behaviors!
 */
class DavFile extends File {

    // Overridden static fields

    /** {@inheritDoc}
     *  For WebDAV we arbitrarily choose ":", although technically lists of URLs are space-separated.
     */
    public static String pathSeparator = ":";

    /** {@inheritDoc}
     *  WebDAV uses URI-style "/" as its separator.
     */
    public static String separator = "/";

    // Private state

    private DavResource resource;

    
    DavFile(DavResource resource) {
        super(resource.toString());

        this.resource = resource;
    }

    // Every method of File must be overridden to hide native OS dependencies

    /** @return false */
    @Override public boolean canExecute() {
        return false;
    }

    /** @return true */
    @Override public boolean canRead() {
        return true;
    }

    /** @return true */
    @Override public boolean canWrite() {
        return true;
    }

    /** @throws UnsupportedOperationException */
    @Override public int compareTo(File pathname) {
        return resource.toString().compareTo(pathname.toString());
    }

    /** @return true */
    @Override public boolean exists() {
        return true;
    }

    @Override public boolean isDirectory() {
        return resource.isDirectory();
    }

    @Override public boolean isFile() {
        return !resource.isDirectory();
    }

    /** @return whether the name starts with a dot (".") */
    @Override public boolean isHidden() {
        return resource.getName().startsWith(".");
    }

    @Override public long lastModified() {
        Date modified = resource.getModified();
        return (modified == null) ? 0L : modified.getTime();
    }

    // Methods override from Object

    @Override public boolean equals(Object other) {
        if (other == null || !other.getClass().equals(getClass())) {
            return false;
        }

        return this.resource.equals(((DavFile) other).resource);
    }

    @Override public int hashCode() {
        return resource.hashCode();
    }

    @Override public String toString() {
        return resource.getName();
    }
}
