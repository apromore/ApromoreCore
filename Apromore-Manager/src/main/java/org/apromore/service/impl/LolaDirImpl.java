package org.apromore.service.impl;


import org.apromore.service.LolaDirBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;

/**
 * Created by corno on 24/07/2014.
 */

public class LolaDirImpl implements LolaDirBean {

    private String lolaDir;

//    public LolaDirImpl(){}

    public LolaDirImpl(String lolaDir){
        this.lolaDir=lolaDir;
    }

    @Override
    public String getLolaDir(){
        return lolaDir;
    }

}
