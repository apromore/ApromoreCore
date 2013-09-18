package org.apromore.filestore.client;

import java.io.InputStream;
import java.util.List;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

/**
 * Implementation of the Apromore File Store Client.
 *
 * @author Cameron James
 */
public class FileStoreServiceClient implements FileStoreService {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";


    /**
     * @see FileStoreService#list(String)
     * {@inheritDoc}
     */
    @Override
    public List<DavResource> list(String folderLocation) throws Exception {
        Sardine sardine = SardineFactory.begin(USERNAME, PASSWORD);
        return sardine.list(folderLocation);
    }

    /**
     * @see FileStoreService#exists(String)
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String url) throws Exception {
        Sardine sardine = SardineFactory.begin(USERNAME, PASSWORD);
        return sardine.exists(url);
    }

    /**
     * @see FileStoreService#getFile(String)
     * {@inheritDoc}
     */
    @Override
    public InputStream getFile(String url) throws Exception {
        Sardine sardine = SardineFactory.begin(USERNAME, PASSWORD);
        return sardine.get(url);
    }

    /**
     * @see FileStoreService#put(String, byte[])
     * {@inheritDoc}
     */
    @Override
    public void put(String url, byte[] data) throws Exception {
        SardineFactory.begin(USERNAME, PASSWORD).put(url, data);
    }

    /**
     * @see FileStoreService#put(String, byte[], String)
     * {@inheritDoc}
     */
    @Override
    public void put(String url, byte[] data, String contentType) throws Exception {
        SardineFactory.begin(USERNAME, PASSWORD).put(url, data, contentType);
    }

    /**
     * @see FileStoreService#createFolder(String)
     * {@inheritDoc}
     */
    @Override
    public void createFolder(String url) throws Exception {
        SardineFactory.begin(USERNAME, PASSWORD).createDirectory(url);
    }

    /**
     * @see FileStoreService#delete(String)
     * {@inheritDoc}
     */
    @Override
    public void delete(String url) throws Exception {
        SardineFactory.begin(USERNAME, PASSWORD).delete(url);
    }

}
