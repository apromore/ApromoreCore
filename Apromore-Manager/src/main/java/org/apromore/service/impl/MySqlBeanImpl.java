package org.apromore.service.impl;

import org.apromore.service.MySqlBean;

/**
 * Created by corno on 23/08/2014.
 */
public class MySqlBeanImpl implements MySqlBean {
    private  String url;
    private  String user;
    private  String password;

    public MySqlBeanImpl(){}

    public MySqlBeanImpl(String url, String user, String password){
        this.url=url;
        this.user=user;
        this.password=password;
    }
    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getURL() {
        return url;
    }
}
