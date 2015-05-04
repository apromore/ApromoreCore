package org.apromore.portal.client;

import org.apromore.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.bind.JAXBElement;
import java.util.List;

/**
 * Created by corno on 9/07/2014.
 */
public class PortalServiceClient implements PortalService{
    private static final Logger LOGGER = LoggerFactory.getLogger(PortalServiceClient.class);
    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    private WebServiceTemplate webServiceTemplate;

    public PortalServiceClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public void addNewTab(List<ResultPQL> result, String userID, List<Detail> details, String query, String nameQuery) {
//        LOGGER.trace("------------------PortalServiceClient: "+query);
//        LOGGER.info("------------------PortalServiceClient: "+query);

        TabAPQLInputMsgType msg= new TabAPQLInputMsgType();
        msg.getResults().addAll(result);
        msg.setUserID(userID);
        msg.getDetails().addAll(details);
        msg.setQuery(query);
        msg.setNameQuery(nameQuery);

        JAXBElement<TabAPQLInputMsgType> request = WS_CLIENT_FACTORY.createTabAPQLRequest(msg);
        JAXBElement<TabAPQLOutputMsgType> response = (JAXBElement<TabAPQLOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if(response.getValue().getResult().getCode() == 0) {
            LOGGER.debug("------------------PortalServiceClientFine");
        }else {
            LOGGER.debug(response.getValue().getResult().getMessage());
        }
    }
}
