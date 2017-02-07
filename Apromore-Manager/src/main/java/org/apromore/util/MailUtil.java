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

package org.apromore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Utility to Help Sending Emails.
 *
 * @author Cameron James
 * @since 1.0
 */
public class MailUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailUtil.class);

    /**
     * Method that will send and email to an address from an address with custom subject and body.
     * @param to to recipients emails address.
     * @param from from the senders email address (most likely apromore@qut.edu.au)
     * @param subject the subject of the email
     * @param bodyMessage the body message for the email.
     */
    public static void sendEmailText(String to, String from, String subject, String bodyMessage) {
        Properties props = getEmailServerProperties();
        Session session = getEmailServerSession(props);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(bodyMessage);

            Transport.send(message);

            LOGGER.debug("Sent Email to " + to + " Done!");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Method that will send and email to an address from an address with custom subject and HTML as the body.
     * @param to to recipients emails address.
     * @param from from the senders email address (most likely apromore@qut.edu.au)
     * @param subject the subject of the email
     * @param bodyMessage the body message for the email with HTML tag inside.
     */
    public static void sendEmailHtml(String to, String from, String subject, String bodyMessage) {
        Properties props = getEmailServerProperties();
        Session session = getEmailServerSession(props);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(bodyMessage, "text/html");

            Transport.send(message);

            LOGGER.debug("Sent Email to " + to + " Done!");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    /* Creates the properties used to connect to the email server. */
    private static Properties getEmailServerProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail-relay.qut.edu.au");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "true");
        return props;
    }

    /* Creates a session with the email server. */
    private static Session getEmailServerSession(Properties props) {
        return Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("username", "password");
                    }
                });
    }
}
