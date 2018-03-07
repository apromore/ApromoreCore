/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
