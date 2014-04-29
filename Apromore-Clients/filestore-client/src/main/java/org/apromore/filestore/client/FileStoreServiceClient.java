package org.apromore.filestore.client;

import java.io.InputStream;
import java.util.List;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.apromore.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.bind.JAXBElement;

/**
 * Implementation of the Apromore File Store Client.
 *
 * @author Cameron James
 */
public class FileStoreServiceClient implements FileStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileStoreServiceClient.class);
    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    private WebServiceTemplate webServiceTemplate;

    /**
     * Default Constructor.
     *
     * @param webServiceTemplate the webservice template
     */
    public FileStoreServiceClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

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

    /**
     * @see org.apromore.filestore.client.FileStoreService#readUserByEmail(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UserType readUserByEmail(String email) throws Exception {
        LOGGER.debug("Preparing ResetUserRequest.....");

        ReadUserByEmailInputMsgType msg = new ReadUserByEmailInputMsgType();
        msg.setEmail(email);

        JAXBElement<ReadUserByEmailInputMsgType> request = WS_CLIENT_FACTORY.createReadUserByEmailRequest(msg);

        JAXBElement<ReadUserByEmailOutputMsgType> response = (JAXBElement<ReadUserByEmailOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getUser();
        }
    }

    /**
     * @see org.apromore.filestore.client.FileStoreService#resetUserPassword(String, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean resetUserPassword(String username, String password) {
        LOGGER.debug("Preparing Reset the Users Password.....");

        ResetUserPasswordInputMsgType msg = new ResetUserPasswordInputMsgType();
        msg.setUsername(username);
        msg.setPassword(password);

        JAXBElement<ResetUserPasswordInputMsgType> request = WS_CLIENT_FACTORY.createResetUserPasswordRequest(msg);

        JAXBElement<ResetUserPasswordOutputMsgType> response = (JAXBElement<ResetUserPasswordOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().isSuccess();
    }

    /**
     * @see org.apromore.filestore.client.FileStoreService#writeUser(org.apromore.model.UserType)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UserType writeUser(UserType user) throws Exception {
        LOGGER.debug("Preparing WriteUserRequest.....");

        WriteUserInputMsgType msg = new WriteUserInputMsgType();
        msg.setUser(user);

        JAXBElement<WriteUserInputMsgType> request = WS_CLIENT_FACTORY.createWriteUserRequest(msg);

        JAXBElement<WriteUserOutputMsgType> response = (JAXBElement<WriteUserOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }

        return response.getValue().getUser();
    }
}
