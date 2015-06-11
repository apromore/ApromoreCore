package com.apql.Apql.popup;

import com.apql.Apql.controller.ViewController;

import javax.swing.*;

/**
 * Created by corno on 29/07/2014.
 */
public class FolderLabel extends JLabel {
    private int id;
    private ImageIcon image=ViewController.getController().getImageIcon(ViewController.ICONFOLDER);

    public FolderLabel(int id, String name){
        super(name, JLabel.LEFT);
        this.id=id;
        setIcon(image);
        setToolTipText(name);
    }

    public int getFolderId(){
        return id;
    }
}
