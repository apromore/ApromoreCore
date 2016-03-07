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

package com.apql.Apql.history;

import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Created by corno on 21/08/2014.
 */
public class QueueHistory {
    private LinkedList<String> queue;
    private int size;
    private int startPos=0, endPos=0;
    private long lastUpdate;

    public QueueHistory(int dimension){
        if(dimension <= 0)
            throw new IllegalArgumentException();
        queue=new LinkedList<>();
        this.size=dimension;
    }

    public void addHistory(String history){
        if(queue.isEmpty()) {
            queue.addLast(history);
            return;
        }
        StringTokenizer st=new StringTokenizer(history," ");
        int nTokenHistory=st.countTokens();
        int nTokenLast=0;
        if(!queue.isEmpty()){
            st=new StringTokenizer(queue.getLast()," ");
            nTokenLast=st.countTokens();
//            System.out.println("TOKEN: "+nTokenHistory+" "+nTokenLast);
            if(queue.size() > size) {
                endPos--;
                queue.removeFirst();
            }
            if(nTokenHistory != nTokenLast) {
                queue.addLast(history);
                endPos++;
            }else if(nTokenHistory == nTokenLast){
                queue.removeLast();
                queue.addLast(history);
            }

        }

//        long currentUpdate=System.currentTimeMillis();
//        if(currentUpdate - lastUpdate > 500) {
//            String result = "";
//            for (int i = 0; i < history.length(); i++) {
//                if (history.charAt(i) != 13)
//                    result += history.charAt(i);
//            }
//            lastUpdate=currentUpdate;
//            queue.addLast(result);
//            if (queue.size() > size)
//                queue.removeFirst();
//        }
    }

    public String undo(){
        if(endPos - 1 > 0){
            endPos = endPos - 1;
            return queue.get(endPos);
        }
        if(!queue.isEmpty())
            return queue.getFirst();
        return "";
    }

    public String redo(){
        if(endPos + 1 < size-1 && queue.size() > endPos + 1){
            endPos = endPos + 1;
            return queue.get(endPos);
        }
        if(!queue.isEmpty())
            return queue.getLast();
        return "";
    }
}
