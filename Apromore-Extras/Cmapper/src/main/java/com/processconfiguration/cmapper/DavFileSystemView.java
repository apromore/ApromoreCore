package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
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
    private final String baseURLString;
    private final File   ROOT;
    private final File[] ROOTS;

    /**
     * Sole constructor.
     *
     * @param service
     */
    DavFileSystemView(final FileStoreService service) {
        this.service       = service;
        this.baseURLString = "http://admin:password@localhost:9000/filestore/dav";
        this.ROOT          = new File("/filestore/dav/");
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
            URI uri = new URI("http", "admin:password", "localhost", 9000, containingDir + "/newFolder", null, null);
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
            for (DavResource resource: service.list("http://admin:password@localhost:9000/filestore/dav" + dir)) {
                LOGGER.info("  Got file " + resource);
                fileList.add(new File(resource.toString()));
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
     * @return false always
     */
    @Override
    public boolean isFloppyDrive(File f) {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @return false always
     */
    @Override
    public boolean isHiddenFile(File f) {
        return false;
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
     * @return true always
     */
    @Override
    public Boolean isTraversable(File f) {
        return true;
    }
}
