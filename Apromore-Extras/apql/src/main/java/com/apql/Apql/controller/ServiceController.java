package com.apql.Apql.controller;

import org.apromore.filestore.client.FileStoreService;
import org.apromore.manager.client.ManagerService;
import org.apromore.portal.client.PortalService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by corno on 23/07/2014.
 */
public class ServiceController {
    private static ManagerService manager;
    private static PortalService portal;
    private static FileStoreService filestore;

    private ServiceController(){
    }

    public static ManagerService getManagerService(){
        if(manager==null){
            ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/managerClientContext.xml");
            manager=(ManagerService)context.getAutowireCapableBeanFactory().getBean("managerClientExternal");
        }
        return manager;
    }

    public static PortalService getPortalService(){
        if(portal==null){
            ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/portalClientContext.xml");
            portal=(PortalService)context.getAutowireCapableBeanFactory().getBean("portalClientExternal");
        }
        return portal;
    }

    public static FileStoreService getFileStoreService(){
        if(filestore==null){
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/filestoreClientContext.xml");
            filestore = (FileStoreService) applicationContext.getAutowireCapableBeanFactory().getBean("fileStoreClientExternal");
        }
        return filestore;
    }
}
