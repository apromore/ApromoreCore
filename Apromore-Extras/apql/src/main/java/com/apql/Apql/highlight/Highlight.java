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

import com.apql.Apql.QueryText;
import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.StringTokenizer;

/**
 * Created by corno on 28/06/2014.
 */
public class Highlight {
    private QueryController queryController;
    private JTextPane textPane;
    private StyledDocument doc;
    private final SimpleAttributeSet keyWord = new SimpleAttributeSet();
    private final SimpleAttributeSet normalWord = new SimpleAttributeSet();
    private final SimpleAttributeSet selectClause = new SimpleAttributeSet();
    private static Highlight high;

    private Highlight(){
        this.queryController=QueryController.getQueryController();
        this.textPane =queryController.getTextPane();
        StyleConstants.setForeground(keyWord, Color.RED);
        StyleConstants.setForeground(selectClause, Color.BLUE);
        StyleConstants.setBold(keyWord, true);
        StyleConstants.setItalic(selectClause, true);
        StyleConstants.setForeground(normalWord, Color.BLACK);
    }

    public static Highlight getHighlight(){
        if(high==null){
            high=new Highlight();
        }
        return high;
    }

    public void highlight(String str){
        int pos = 0;
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != 13)
                result += str.charAt(i);
        }

//        int indexSELECT = result.indexOf(Keywords.SELECT);
//        int indexFROM = result.indexOf(Keywords.FROM);
//        int indexWHERE = result.indexOf(Keywords.WHERE);

        StringBuilder sb = new StringBuilder();
        queryController.getTextPane().setText("");
        this.doc=queryController.getTextPane().getStyledDocument();
        StringTokenizer st = new StringTokenizer(result, " \b\n\t\r(){}[],~\"", true);
        String tmpString;
        while (st.hasMoreTokens()) {
            tmpString = st.nextToken();
            if(tmpString.equals("\"")){
                String tmp=tmpString;
//                System.out.println(">>>>>>>>>>>>>>>>TMP: "+tmp+" "+st.hasMoreTokens());
                if(st.hasMoreTokens()) {
                    tmpString=st.nextToken();
                    tmp+=tmpString;
//                    System.out.println(">>>>>>>>>>>>>>>>TMPIF: "+tmp+" "+st.hasMoreTokens());
                    while (st.hasMoreTokens() && !tmpString.equals("\"")) {
//                        System.out.println(">>>>>>>>>>>>>>>>TMPWHILE: "+tmp+" "+st.hasMoreTokens());
                        tmpString=st.nextToken();
                        tmp+=tmpString;
                    }
                }
                colorWord(pos, tmp, normalWord);
//                System.out.println(">>>>>>>>>>>>>>>>TMP: "+tmp+" "+result);
                pos+=tmp.length();
            }else
            if (Keywords.contains(tmpString)) {
                if (tmpString.equalsIgnoreCase(Keywords.CAN_OCCUR)) {
                    colorWord(pos, Keywords.CAN_OCCUR, keyWord);
                } else if (tmpString.equalsIgnoreCase(Keywords.CAN_COOCCUR)) {
                    colorWord(pos, Keywords.CAN_COOCCUR, keyWord);
                } else if (tmpString.equalsIgnoreCase(Keywords.CAN_CONFLICT)) {
                    colorWord(pos, Keywords.CAN_CONFLICT, keyWord);
                } else if (tmpString.equalsIgnoreCase(Keywords.COOCCUR)) {
                    colorWord(pos, Keywords.COOCCUR, keyWord);
                } else if (tmpString.equalsIgnoreCase(Keywords.CONFLICT)) {
                    colorWord(pos, Keywords.CONFLICT, keyWord);
                } else if (tmpString.equalsIgnoreCase(Keywords.ALWAYS_OCCURS)) {
                    colorWord(pos, Keywords.ALWAYS_OCCURS, keyWord);
                } else if(tmpString.equalsIgnoreCase(Keywords.GET_TASKS)){
                    colorWord(pos, Keywords.GET_TASKS, keyWord);
                } else if(tmpString.equalsIgnoreCase(Keywords.TOTAL_CAUSAL)){
                    colorWord(pos, Keywords.TOTAL_CAUSAL, keyWord);
                } else if(tmpString.equalsIgnoreCase(Keywords.TOTAL_CONCURRENT)){
                    colorWord(pos, Keywords.TOTAL_CONCURRENT, keyWord);
                } else if ((tmpString.equals("*") || tmpString.equals("id") || tmpString.equals("name") || tmpString.equals("language") || tmpString.equals("version") || tmpString.equals("owner") || tmpString.equals("domain") || tmpString.equals("ranking"))) {
                    colorWord(pos, tmpString, selectClause);
                } else {
                    colorWord(pos, tmpString.toUpperCase(), keyWord);
                }
                pos += tmpString.length();
            } else {
                colorWord(pos, tmpString, normalWord);
                pos += tmpString.length();
            }
        }
    }

    private void colorWord(int pos, String word, SimpleAttributeSet color) {
        try {
            this.doc.insertString(pos, word, color);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

}
