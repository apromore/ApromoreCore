/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

package com.apql.Apql.highlight;

import com.apql.Apql.controller.ContextController;
import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import com.apql.Apql.popup.PopupFrame;

import javax.swing.*;
import javax.swing.text.TextAction;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;

/**
 * Created by corno on 4/08/2014.
 */
public class ButtonAction {

    public static class CTRLBack extends TextAction{
        private static HashSet<String> locationInQuery;
        private static boolean expand=true;
        private static String query;
        private CTRLBack ctrlBack;
//        private char u2026=20D;

        public CTRLBack(){
            super("expandCollapse");
        }

        public void actionPerformed(ActionEvent e) {
            QueryController queryController=QueryController.getQueryController();
            HashSet<String> locationInQuery = queryController.keepLocationInQuery();
            System.out.println("ACTION EC");
            if(queryController.malformedQuery()) {
                System.out.println("EC MALFORMED");
                return;
            }

            if(expand && !locationInQuery.isEmpty()){
                query=queryController.getTextPane().getText();
                System.out.println("IF expand EC");
                expand=false;
                CTRLBack.locationInQuery=locationInQuery;
                Highlight.getHighlight().highlight(queryController.getBeforeLoc()+" + "+queryController.getAfterLoc());
            }else if(!expand && queryController.keepLocationInQuery().contains("+")){
                System.out.println("ELSE expand EC");
                StringBuilder locations=new StringBuilder();
                expand=true;
                for(String str : this.locationInQuery){
                    locations.append("\""+str+"\", ");
                }
                locations.delete(locations.length()-2,locations.length()-1);
                Highlight.getHighlight().highlight(queryController.getBeforeLoc()+" "+locations.toString()+" "+queryController.getAfterLoc());
            }
        }

        public static HashSet<String> getLocationInQuery(){
           return locationInQuery;
        }

        public static void setLocationInQuery(HashSet<String> locations){
            locationInQuery=locations;
        }

        public static String getQuery(){
            if(query==null)
                return QueryController.getQueryController().getTextPane().getText();
            return query;
        }

        public static void setQuery(String queryStr){
            query=queryStr;
        }

        public static void setExpand(boolean value){
            expand=value;
        }
    }

    public static class CTRLZ extends TextAction{
        private  QueryController queryController = QueryController.getQueryController();

        public CTRLZ(){
            super("undo");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Highlight.getHighlight().highlight(queryController.getQueueHistory().undo());
        }
    }

    public static class CTRLY extends TextAction{
        private  QueryController queryController = QueryController.getQueryController();

        public CTRLY(){
            super("redo");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Highlight.getHighlight().highlight(queryController.getQueueHistory().redo());
        }
    }

    public static class CTRLSpace extends TextAction {
        private ViewController viewController=ViewController.getController();
        private PopupFrame popup;

        public CTRLSpace() {
            super("suggest");
        }

        public void actionPerformed(ActionEvent e) {
            if (!viewController.isPopupOpen()) {
                popup = new PopupFrame();
                JScrollPane pane=ContextController.getContextController().findContext();
                if(pane.getMinimumSize().getHeight() <=150) {
                    popup.setPreferredSize(new Dimension(200, (int) pane.getMinimumSize().getHeight()));
                    popup.setMaximumSize(new Dimension(200, (int) pane.getMinimumSize().getHeight()));
                    popup.setMinimumSize(new Dimension(200, (int) pane.getMinimumSize().getHeight()));
                }else{
                    popup.setPreferredSize(new Dimension(200, 150));
                    popup.setMaximumSize(new Dimension(200, 150));
                    popup.setMinimumSize(new Dimension(200, 150));
                }
                popup.add(pane);
                viewController.setPopup(popup);
                popup.pack();
                popup.setVisible(true);
                viewController.setPopupOpen(true);
            }
        }

    }

    public static class CTRLV extends TextAction {
        private JTextPane pane;

        public CTRLV(JTextPane pane) {
            super("paste");
            this.pane=pane;
        }

        public void actionPerformed(ActionEvent e) {
            pane.paste();
        }
    }

    public static class CTRLC extends TextAction {
        private JTextPane pane;

        public CTRLC(JTextPane pane) {
            super("copy");
            this.pane=pane;
        }

        public void actionPerformed(ActionEvent e) {
            pane.copy();
        }
    }

    public static class CTRLX extends TextAction {
        private JTextPane pane;

        public CTRLX(JTextPane pane) {
            super("cut");
            this.pane=pane;
        }

        public void actionPerformed(ActionEvent e) {
            pane.cut();
        }
    }

    public static class CTRLA extends TextAction {
        private JTextPane pane;

        public CTRLA(JTextPane pane) {
            super("selectAll");
            this.pane=pane;
        }

        public void actionPerformed(ActionEvent e) {
            pane.selectAll();
        }
    }
}
