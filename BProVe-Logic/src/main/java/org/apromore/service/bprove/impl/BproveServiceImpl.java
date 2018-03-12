/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.service.bprove.impl;
import plugin.bpmn.to.maude.handlers.PostMultipleParameters;
import plugin.bpmn.to.maude.notation.*;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.ArrayList;

import org.apromore.service.bprove.BproveService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import java.io.ObjectOutputStream;

import plugin.bpmn.to.maude.handlers.MaudeOperationEditor;

/**
 * Created by Fabrizio Fornari on 18/05/2017.
 */
@Service
public class BproveServiceImpl implements BproveService {

    @Override
    public String getParsedModelBprove(
            String modelString
            //BPMNDiagram bpmnDiagram
            ){

            String branch = null;
            String nativeType = null;
            String annName = null;

            String resultBprove=null;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();


            try{

              resultBprove = GetBProVefromStringModel(modelString);

            }catch(Exception e2){
              System.err.println("Unable to Receive BProVe results: " + e2.getMessage());
              StringWriter errors = new StringWriter();
              e2.printStackTrace(new PrintWriter(errors));

              e2.printStackTrace();
            }
            return resultBprove;

            }


            public static String GetBProVefromStringModel( String modelString ) throws MalformedURLException, IOException {
                PostMultipleParameters parsedModel = new PostMultipleParameters();
                //JOptionPane.showMessageDialog(null,"\n inside GetBProVefromStringModel");
                try {

                    PostMultipleParameters inputM = new PostMultipleParameters();

                    inputM.setOriginalModel(modelString);

                    parsedModel=PostReq_BProve_Maude_WebService_Property(inputM);

                } catch (Exception e1) {
                   e1.printStackTrace();
                    //JOptionPane.showMessageDialog(null,"\n e1.printStackTrace()");
                }

                if (parsedModel.getParsedModel().contains("Error")){
                    //JOptionPane.showMessageDialog(null, "\nThe parsing encountered some problems. Check if the webservice is still online\n"+parsedModel);
                }

                if (parsedModel.getParsedModel().contains("Correct Duplicated Labels")){
                    //JOptionPane.showMessageDialog(null, "\nThe parsing encountered some problems.\n"+parsedModel);
                }

                return parsedModel.getParsedModel();


            }

    public static PostMultipleParameters PostReq_BProve_Maude_WebService_Property( PostMultipleParameters inputM) throws Exception {
        String address=null;
        String defaultAddress = "http://pros.unicam.it:8080/";
        //String defaultAddress = "http://localhost:8080/";
        boolean parse = false;
        if(inputM.getProperty()==null) {
        	address = defaultAddress+"BProVe_WebService/webapi/BPMNOS/parseModel";
            parse = true;
        }else{
            address = defaultAddress+"BProVe_WebService/webapi/BPMNOS/model/verification";
            parse = false;
        }

        PostMultipleParameters resultM = new PostMultipleParameters();
        try {
            ObjectOutputStream out;
            URL myurl = new URL(address);
            HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/xml;");
            con.setRequestProperty("Accept", "application/xml");
            con.setRequestProperty("Method", "POST");
            out = new ObjectOutputStream(con.getOutputStream());
            out.writeObject(inputM);


            ///Handling the response from the server
            StringBuilder result = new StringBuilder();
            String resultString = null;
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));

                InputStream is = con.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                ByteArrayInputStream in = new ByteArrayInputStream(buffer.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(in);
                resultM = (PostMultipleParameters) ois.readObject();

            }
            else {
               // JOptionPane.showMessageDialog(null, "response\n" + con.getResponseCode());
                System.out.println(con.getResponseCode());
                System.out.println(con.getResponseMessage());
            }
            out.close();
        } catch (Exception e2) {
            e2.printStackTrace();
           // JOptionPane.showMessageDialog(null, "e2.printStackTrace();\n" + e2);
        }

        return resultM;

    }



//MaudeOperation
    @Override
    public String getMaudeOperation(
        String modelToParse, String parsedModel, String propertyToVerify, 
    String param, String poolName1, String poolName2, String taskName1 , String taskName2 , String msgName
        //BPMNDiagram bpmnDiagram
    ){
        String result=null;
        try{
            result = MaudeOperationEditor.doMaudeOperation( modelToParse, parsedModel, propertyToVerify,  param,  poolName1,  poolName2,  taskName1 ,  taskName2 ,  msgName);
        } catch (Exception e3) {
            e3.printStackTrace();
            JOptionPane.showMessageDialog(null, "MaudeOperationEditor.doMaudeOperation e3.printStackTrace();\n" + e3.getMessage());
        }
            return result;
    }

}
