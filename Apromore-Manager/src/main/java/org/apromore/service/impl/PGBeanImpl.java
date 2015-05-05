package org.apromore.service.impl;

import org.apromore.service.PGBean;

/**
 * Created by corno on 23/08/2014.
 */
public class PGBeanImpl implements PGBean{
    private String host;
    private String name;
    private String user;
    private String password;

//    public PGBeanImpl(){}

    public PGBeanImpl(String host,String name,String user,String password){
        this.host=host;
        this.name=name;
        this.user=user;
        this.password=password;
    }
    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
