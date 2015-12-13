/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.apql.Apql.tree;

import com.apql.Apql.controller.QueryController;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by corno on 2/08/2014.
 */
public class DraggableNodeProcess extends DraggableNodeTree {

//    private List<VersionSummaryType> versions;
    private String latestVersion;
//    private String originalLanguage;
    private String latestBranch;
//    private String domain;
//    private String ranking;
    private ProcessSummaryType pst;
    private QueryController queryController=QueryController.getQueryController();

    public DraggableNodeProcess(ProcessSummaryType pst, String path){
        super(pst.getId().toString(),pst.getName(),path);
//        this.versions=pst.getVersionSummaries();
        String lastUpdate=null;
        VersionSummaryType version=null;
        for(VersionSummaryType vst : pst.getVersionSummaries()) {
            if (lastUpdate == null || vst.getLastUpdate().compareTo(lastUpdate) > 0) {
                lastUpdate = vst.getLastUpdate();
                version = vst;
            }
            queryController.addLocation(pst.getId()+"/"+vst.getVersionNumber()+"/"+vst.getName(),this);
        }
        this.pst=pst;
        this.latestVersion=version.getVersionNumber();
        this.latestBranch=version.getName();

//        this.originalLanguage=pst.getOriginalNativeType();
//        this.domain=pst.getDomain();
//        this.ranking=pst.getRanking();
    }

    public ProcessSummaryType getValue(){
        return pst;
    }

    public VersionSummaryType getVersionSummaryType(String idNet){
        StringTokenizer st=new StringTokenizer(idNet,"/");
        String id=st.nextToken();
        String version=st.nextToken();
        String branch=st.nextToken();
        for(VersionSummaryType vst : pst.getVersionSummaries()){
            if(!id.equals(pst.getId().toString()))
                throw new IllegalArgumentException("Wrong ID net");
            if(vst.getVersionNumber().equals(version) && vst.getName().equals(branch)){
                return vst;
            }
        }
        return null;
    }

    public List<VersionSummaryType> getVersions() {
        return pst.getVersionSummaries();
    }

    public void setVersions(List<VersionSummaryType> versions) {
        this.pst.getVersionSummaries().clear();
        this.pst.getVersionSummaries().addAll(versions);
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setVersionBranch(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getLatestBranch() {
        return latestBranch;
    }

    public void setLatestBranch(String latestBranch) {
        this.latestBranch = latestBranch;
    }

    public String getOriginalLanguage() {
        return pst.getOriginalNativeType();
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.pst.setOriginalNativeType(originalLanguage);
    }

    public String getDomain() {
        return pst.getDomain();
    }

    public void setDomain(String domain) {
        this.pst.setDomain(domain);
    }

    public String getRanking() {
        return pst.getRanking();
    }

    public void setRanking(String ranking) {
        this.pst.setRanking(ranking);
    }


    public boolean equals(Object ob){
        if(ob==null || !(ob instanceof DraggableNodeProcess)){
            return false;
        }
        if(ob == this)
            return true;
        DraggableNodeProcess dnp=(DraggableNodeProcess)ob;
        return dnp.getId().equals(getId()) && dnp.getLatestVersion().equals(latestVersion) && dnp.getLatestBranch().equals(latestBranch);
    }

    public int hashCode(){
        return getId().hashCode()*latestVersion.hashCode()*latestBranch.hashCode();
    }

}
