package org.apromore.service.bebop.impl;
/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
//

import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.StringReader;
//
//
import javax.faces.context.FacesContext;

public class GetReqGuidelines {

    public static ArrayList<String> GetBEBoPfromStringModel( String modelString ) throws MalformedURLException, IOException {

        String target = "http://90.147.167.207:8080/verification-component-understandability-plugin-1.0/validatemodel/put/?en";

        //connection to the server and post of the model in String format
        URL myurl = new URL(target);
        HttpURLConnection con = (HttpURLConnection)myurl.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "text/plain;");
        con.setRequestProperty("Accept", "text/plain");
        con.setRequestProperty("Method", "POST");
        OutputStream os = con.getOutputStream();
        os.write(modelString.getBytes("UTF-8"));
        os.close();

        //Handling the response from the server
        StringBuilder id = new StringBuilder();
        int HttpResult =con.getResponseCode();
        if(HttpResult ==HttpURLConnection.HTTP_OK){
            BufferedReader br = new BufferedReader(new   InputStreamReader(con.getInputStream(),"utf-8"));

            String line = null;
            while ((line = br.readLine()) != null) {id.append(line + "\n");}
            br.close();
            System.out.println(""+id.toString());

        }else{
            System.out.println(con.getResponseCode());
            System.out.println(con.getResponseMessage());
        }

        //second connection to the server
        //GET the id of the moel stored on the server
        target = "http://90.147.167.207:8080/verification-component-understandability-plugin-1.0/validatemodel/"+id;
        URL obj = new URL(target);
        HttpURLConnection con2 = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con2.setRequestMethod("GET");

        int responseCode = con2.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + target);
        System.out.println("Response Code : " + responseCode);

        //Handling the response from the server
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con2.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {	response.append(inputLine);}
        in.close();

        System.out.println(response.toString());

        try{

            JAXBContext jaxbContext = JAXBContext.newInstance(GuidelinesFactory.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(response.toString());
            GuidelinesFactory glres = (GuidelinesFactory) unmarshaller.unmarshal(reader);
            ArrayList<String> ret =glres.getElementsIDs();
            return ret;

        }catch( Exception e){

            ArrayList<String> excpt = new ArrayList <String>();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.toString();
            excpt.add("Exception ");
            excpt.add(sw.toString());
            return excpt;

        }
    }
}
