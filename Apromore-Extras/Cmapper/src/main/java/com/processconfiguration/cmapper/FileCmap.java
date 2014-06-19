package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

// Local classes
import com.processconfiguration.cmap.CMAP;

/**
 * Configuration mapping stored on the local filesystem.
 */
public class FileCmap implements Cmap {

    private CMAP cmap;
    private File file;

    /**
     * Sole constructor.
     */
    public FileCmap(File file) throws Exception {
        this.file = file;
    }

    /**
     * {@inheritDoc}
     */
    public CMAP getCmap() throws Exception {
        JAXBContext jc = JAXBContext.newInstance("com.processconfiguration.cmap");
        Unmarshaller u = jc.createUnmarshaller();
        this.cmap = (CMAP) u.unmarshal(file);

        return cmap;
    }

    /**
     * {@inheritDoc}
     */
    public OutputStream getOutputStream() throws Exception {
        return new FileOutputStream(file);
    }

    /**
     * {@inheritDoc}
     */
    public URI getURI() {
        return file.toURI();
    }
}

