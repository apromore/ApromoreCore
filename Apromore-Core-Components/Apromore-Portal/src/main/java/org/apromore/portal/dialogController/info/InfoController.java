/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.portal.dialogController.info;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * This is a simple ZUL to display the clients info on the screen.
 * This is used for debugging issues with the application.
 *
 * @author Cameron James
 */
public class InfoController extends GenericForwardComposer {

    private Textbox txtSessionInfo;
    private StringBuilder clientInfo = new StringBuilder();

    @Override
    @SuppressWarnings("unchecked")
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
    }

    public void onClick$btnExec(Event evt) throws InterruptedException {
        collectInfo();
    }

    public void onClientInfo$info(ClientInfoEvent evt) {
        clientInfo.append("ZK ClientInfo: \r");
        clientInfo.append("getScreenWidth():\t\t").append(evt.getScreenWidth()).append(" x ").append(evt.getScreenHeight()).append("\r");
        clientInfo.append("getColorDepth():\t\t").append(evt.getColorDepth()).append("bit\r");
        clientInfo.append("getDesktopWidth():\t\t").append(evt.getDesktopWidth()).append(" x ").append(evt.getDesktopHeight()).append("\r");
        clientInfo.append("getTimeZone():\t\t\t").append(evt.getTimeZone().getDisplayName()).append("\r");
        clientInfo.append("getName():\t\t\t").append(evt.getName()).append("\r");
        clientInfo.append("--------------------------------------------------------------------------------------------------\r");
    }

    private void collectInfo() {
        StringBuilder result = new StringBuilder();

        try {
            result.append("--------------------------------------------------------------\r");
            result.append("ZK Session\r");
            Session sess = Sessions.getCurrent();
            result.append(".getLocalAddr():\t\t").append(sess.getLocalAddr()).append("\r");
            result.append(".getLocalName():\t\t").append(sess.getLocalName()).append("\r");
            result.append(".getRemoteAddr():\t\t").append(sess.getRemoteAddr()).append("\r");
            result.append(".getRemoteHost():\t\t").append(sess.getRemoteHost()).append("\r");
            result.append(".getServerName():\t\t").append(sess.getServerName()).append("\r");
            result.append(".getWebApp().getAppName():\t").append(sess.getWebApp().getAppName()).append("\r");

            HttpSession hses = (HttpSession) sess.getNativeSession();
            result.append("--------------------------------------------------------------------------------------------------\r");
            result.append("HttpSession\r");
            result.append(".getId():\t\t\t").append(hses.getId()).append("\r");
            result.append(".getCreationTime():\t\t").append(new Date(hses.getCreationTime()).toString()).append("\r");
            result.append(".getLastAccessedTime():\t\t").append(new Date(hses.getLastAccessedTime()).toString()).append("\r");

            result.append("--------------------------------------------------------------------------------------------------\r");
            result.append("ServletContext\r");
            ServletContext sCon = hses.getServletContext();
            result.append(".getServerInfo():\t\t").append(sCon.getServerInfo()).append("\r");
            result.append(".getContextPath():\t\t").append(sCon.getContextPath()).append("\r");
            result.append(".getServletContextName():\t").append(sCon.getServletContextName()).append("\r");

            result.append("--------------------------------------------------------------------------------------------------\r");
            result.append("ZK Executions\r");
            result.append(".getHeader('user-agent'):\t").append(Executions.getCurrent().getHeader("user-agent")).append("\r");
            result.append(".getHeader('accept-language'):\t").append(Executions.getCurrent().getHeader("accept-language")).append("\r");
            result.append(".getHeader('referer'):\t\t").append(Executions.getCurrent().getHeader("referer")).append("\r");
            result.append(".getHeader('connection'):\t").append(Executions.getCurrent().getHeader("connection")).append("\r");
            result.append(".getHeader('zk-sid'):\t\t").append(Executions.getCurrent().getHeader("zk-sid")).append("\r");
            result.append(".getHeader('origin'):\t\t").append(Executions.getCurrent().getHeader("origin")).append("\r");
            result.append(".getHeader('host'):\t\t").append(Executions.getCurrent().getHeader("host")).append("\r");
            result.append(".getHeader('cookie'):\t\t").append(Executions.getCurrent().getHeader("cookie")).append("\r");
            result.append("--------------------------------------------------------------------------------------------------\r");

            result.append(clientInfo);
            txtSessionInfo.setValue(result.toString());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}