package org.apromore.filestore.client;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import com.github.sardine.DavResource;

import org.apromore.model.UserType;

/**
 * File Service that handles the communication between the web and the dav services.
 *
 * @author Cameron James
 */
public interface FileStoreService {

    /**
     * Retrieves the folders contents for the location passed.
     * <p>
     * example usage: List<DavResource> resources = filestoreService.list("http://yourdavserver.com/adirectory/");
     * </p>
     *
     * @param folderLocation the location we are wanting the contents for.
     * @return the List of objects in the folder.
     * @throws Exception many things could go wrong, so a generic error until i can work out what i should have instead.
     */
    List<DavResource> list(final String folderLocation) throws Exception;


    /**
     * Checks to see if the given url exists on the webdav server.
     * <p>
     * example usage: boolean exists = filestoreService.exists("http://yourdavserver.com/adirectory/afile.jpg");
     * </p>
     *
     * @param url the url to the file or folder.
     * @return true if the file or folder is found otherwise false.
     * @throws Exception many things could go wrong, so a generic error until i can work out what i should have instead.
     */
    boolean exists(final String url) throws Exception;


    /**
     * Retrieves the File and returns the file as an InputStream.
     * <p>
     * example usage: InputStream is = filestoreService.get("http://yourdavserver.com/adirectory/afile.jpg");
     * </p>
     *
     * @param url the url to the file.
     * @return the found file as an inputStream or null.
     * @throws Exception many things could go wrong, so a generic error until i can work out what i should have instead.
     */
    InputStream getFile(final String url) throws Exception;


    /**
     * Uploads a new file to the webdav repo.
     * <p>
     * example usage:
     *   byte[] data = FileUtils.readFileToByteArray(new File("/file/on/disk"));
     *   filestoreService.put("http://yourdavserver.com/adirectory/nameOfFile.jpg", data);
     * </p>
     *
     * @param url the location to place the file.
     * @param data the byte stream that is the file.
     * @throws Exception many things could go wrong, so a generic error until i can work out what i should have instead.
     */
    void put(final String url, final byte[] data) throws Exception;


    /**
     * Uploads a new file to the webdav repo but also specifing the content type.
     * <p>
     * example usage:
     *   byte[] data = FileUtils.readFileToByteArray(new File("/file/on/disk"));
     *   filestoreService.put("http://yourdavserver.com/adirectory/nameOfFile.jpg", data, "image/jpeg");
     * </p>
     *
     * @param url the location to place the file.
     * @param data the byte stream that is the file.
     * @param contentType the content type of this file.
     * @throws Exception many things could go wrong, so a generic error until i can work out what i should have instead.
     */
    void put(final String url, final byte[] data, final String contentType) throws Exception;


    /**
     * Creates a new folder in the webdav repo.
     * <p>
     * example usage: filestoreService.createFolder("http://yourdavserver.com/a/new/folder/");
     * </p>
     *
     * @param url the new location of the folder to create.
     * @throws Exception many things could go wrong, so a generic error until i can work out what i should have instead.
     */
    void createFolder(final String url) throws Exception;


    /**
     * Creates a new folder in the webdav repo.
     * <p>
     * example usage: filestoreService.createFolder("http://yourdavserver.com/a/new/folder/");
     * </p>
     *
     * @param url the new location of the folder or file to delete.
     * @throws Exception many things could go wrong, so a generic error until i can work out what i should have instead.
     */
    void delete(final String url) throws Exception;

    /**
     * Used as a part of the reset user's password.
     *
     * @param email the users email address
     * @return the UserType from the webservice
     */
    UserType readUserByEmail(String email) throws Exception;

    /**
     * Reset the User's password for them.
     *
     * @param username the users username
     * @param password the new password
     * @return if we succeeded or not
     */
    boolean resetUserPassword(String username, String password);

    /**
     * Send the users details to be persisted.
     *
     * @param user the user to send to the WebService
     * @throws Exception ... change to be something more relevant
     * TODO: Fix Exception
     */
    UserType writeUser(UserType user) throws Exception;

    /**
     * The root directory of the filestore, e.g. <code>http://example.com:9000/filestore/dav/</code>.
     *
     * This is useful for resolving relative URLs as arguments to methods that require absolute URLs.
     *
     * Example usage:
     *   FileStoreService fs = ... ;
     *   URI absoluteURI = fs.getBaseURI().resolve("example.txt");
     *   InputStream in = fs.getFile(absoluteURI.toString());
     *
     * @return the URI of the root directory of the filestore
     */
    URI getBaseURI();
}
