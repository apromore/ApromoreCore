package com.apql.Apql.popup;

import com.apql.Apql.controller.ViewController;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;

import javax.swing.*;
import java.util.List;

/**
 * Created by corno on 28/07/2014.
 */
public class ProcessLabel extends JLabel {
    private int id;
    private List<VersionSummaryType> versions;
    private Icon icon;
    private String name;
    private String nativeType;
    private String branch;

    private String owner;

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getNativeType() {
        return nativeType;
    }

    public void setNativeType(String nativeType) {
        this.nativeType = nativeType;
    }

    public List<VersionSummaryType> getVersions() {
        return versions;
    }

    public void setVersions(List<VersionSummaryType> versions) {
        this.versions = versions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Icon getIcon(){
        return this.icon;
    }

    public void setIcon(Icon icon){
        this.icon=icon;
    }

//    public ProcessLabel(int id, List<VersionSummaryType> versions, Icon icon, String name){
//        super(name,icon,JLabel.LEFT);
//        this.id=id;
//        this.versions=versions;
//        this.name=name;
//        this.icon=icon;
//    }

    public ProcessLabel(int id, String name, String nativeType, String branch, List<VersionSummaryType> versions){
        this.id=id;
        this.name=name;
        this.nativeType=nativeType;
        this.versions=versions;
        this.branch=branch;
        this.icon= ViewController.getController().getImageIcon(ViewController.ICONPROCESS);
        setIcon(icon);
        if(name.startsWith("\""))
            setText(name.substring(1,name.length()));
        else
            setText(name);
        setToolTipText(name);
    }


}
