package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import javax.swing.filechooser.FileSystemView;

// Third party classes
import com.github.sardine.DavResource;

// Local classes
import org.apromore.filestore.client.FileStoreService;

/**
 * Allow a {@link FileStoreService} to work as the model of a {@link JFileChooser}.
 */
class DavFileSystemView extends FileSystemView {

    private static Logger LOGGER = Logger.getLogger(DavFileSystemView.class.getCanonicalName());

    private final FileStoreService service;
    private final File   ROOT;
    private final File[] ROOTS;

    /**
     * Sole constructor.
     *
     * @param service
     */
    DavFileSystemView(final FileStoreService service) {
        this.service       = service;
        this.ROOT          = new File(service.getBaseURI().getPath());
        this.ROOTS         = new File[] { ROOT };
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public File createFileObject(File dir, String filename) {
        LOGGER.info("Create file in " + dir + " named " + filename);
        return new File(dir, filename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File createFileObject(String path) {
        LOGGER.info("Create file " + path);
        return new File(path);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public File createFileSystemRoot(File f) {
        throw new UnsupportedOperationException("File system creation not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File createNewFolder(File containingDir) {
        try {
            URI uri = service.getBaseURI().resolve(containingDir + "/newFolder");
            LOGGER.info("Creating folder with uri " + uri);
            service.createFolder(uri.toString());
            return new File(containingDir, "newFolder");

        } catch (Exception e) {
            LOGGER.log(SEVERE, "Unable to create folder in " + containingDir, e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getDefaultDirectory() {
        return ROOT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File[] getFiles(File dir, boolean userFileHiding) {

        LOGGER.info("Get files in " + dir);
        ArrayList<File> fileList = new ArrayList<>();
        try {
            String s = dir.toString();
            URI uri;
            if ("/".equals(s)) {
                uri = service.getBaseURI();
            } else {
                uri = service.getBaseURI().resolve(dir.toString());
            }
            LOGGER.info("Get files at " + uri);
            for (DavResource resource: service.list(uri.toString())) {
                LOGGER.info("  Got file \"" + resource + "\" name=\"" + resource.getName());
                DavFile davFile = new DavFile(resource);
                if (davFile.isHidden()) {
                    LOGGER.info("  Skipping \"" + resource + "\"");
                    continue;
                }
                fileList.add(davFile);
            }
        } catch (Exception e) {
            LOGGER.log(SEVERE, "Unable to iterate through DAV files", e);
            // fall through with the empty fileList
        }

        final File[] dummy = {};
        return fileList.toArray(dummy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getHomeDirectory() {
        return ROOT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getParentDirectory(File dir) {
        return dir.getParentFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File[] getRoots() {
        return ROOTS;
    }

    /**
     * {@inheritDoc}
     *
     * @return true always
     */
    @Override
    public boolean isFileSystem(File f) {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return false always
     */
    @Override
    public boolean isFloppyDrive(File f) {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @return true if the name begins with "."
     */
    @Override
    public boolean isHiddenFile(File f) {
        return f.isHidden();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRoot(File f) {
        return ROOT.equals(f);
    }

    /**
     * {@inheritDoc}
     *
     * @return false always
     */
    @Override
    public Boolean isTraversable(File f) {
        return f.isDirectory();
    }


    /**
     * A file on the WebDAV repository.
     */
    class DavFile extends File {

        private DavResource resource;

        DavFile(DavResource resource) {
            super(resource.toString());

            this.resource = resource;
        }

        @Override public boolean canExecute() {
            return true;
        }

        @Override public boolean canRead() {
            return true;
        }

        @Override public boolean canWrite() {
            return true;
        }

        @Override public boolean exists() {
            return true;
        }

        @Override public boolean isDirectory() {
            return resource.isDirectory();
        }

        @Override public boolean isFile() {
            return !resource.isDirectory();
        }

        @Override public boolean isHidden() {
            return resource.getName().startsWith(".");
        }

        @Override public long lastModified() {
            Date modified = resource.getModified();
            return (modified == null) ? 0L : modified.getTime();
        }
    }
}
